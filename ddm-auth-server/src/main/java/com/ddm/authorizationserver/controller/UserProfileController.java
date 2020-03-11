package com.ddm.authorizationserver.controller;

import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ddm.authorizationserver.model.EntityUserProfileCreation;
import com.ddm.authorizationserver.model.Group;
import com.ddm.authorizationserver.model.ProfileDetails;
import com.ddm.authorizationserver.model.Role;
import com.ddm.authorizationserver.model.User;
import com.ddm.authorizationserver.model.UserGroup;
import com.ddm.authorizationserver.payload.ApiResponse;
import com.ddm.authorizationserver.payload.ProfileCreation;
import com.ddm.authorizationserver.repository.GroupRepository;
import com.ddm.authorizationserver.repository.RoleRepository;
import com.ddm.authorizationserver.repository.UserDetailRepository;
import com.ddm.authorizationserver.repository.UserGroupRepository;

@RestController
@RequestMapping(value ="/api/v1/")
public class UserProfileController {

	private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);
	
	@Autowired
	private UserDetailRepository userRepository;
	
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
	private GroupRepository groupRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
    @Autowired
    private UserGroupRepository  userGroupRepository;

	/**
	 * method to create group_admin, this is be only be allowed for Master Admin role
	 * @return
	 */
	@PostMapping("/user/")
	@PreAuthorize("hasAuthority('MASTER_ADMIN')")
	public ResponseEntity<?> createGroupAdmin(@Valid @RequestBody ProfileCreation profile) {
		logger.info("create user by Master Admin.... createGroupAdmin()");
		if(userRepository.existsByEmail(profile.getEmail()) || userRepository.existsByUsername(profile.getUserName())) {
			return new ResponseEntity<>(new ApiResponse(false, "User already exists"), HttpStatus.BAD_REQUEST);
		}
		
		List<Role> roles = roleRepository.findByName(profile.getRoles());
		
		ProfileDetails profileDetail = new ProfileDetails(profile.getFullName(),profile.getOccupation() , profile.getPan(), profile.getDob(), profile.getMobile(), null);
		Group group = new Group(profile.getFullName());
		Group groupResult = groupRepository.save(group);
		
		User user = new User(profile.getUserName(), passwordEncoder.encode(profile.getPassword()), profile.getEmail(), roles, profileDetail, groupResult);
		User result = userRepository.save(user);

		Set<User> userList = result.getGroup().getUser();
		userList.add(result);
		groupResult.setUser(userList);
		groupRepository.save(groupResult);
		
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/group")
				.buildAndExpand(result).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "User successfully created"));
	}
	
	@PostMapping("/user/groupuser/")
	@PreAuthorize("hasAuthority('GROUP_ADMIN')")
	public ResponseEntity<?> createUser(@Valid @RequestBody ProfileCreation profile) {
		if(userRepository.existsByEmail(profile.getEmail()) || userRepository.existsByUsername(profile.getUserName())) {
			return new ResponseEntity<>(new ApiResponse(false, "User already exists"), HttpStatus.BAD_REQUEST);
		}
		// This should be replaced to annotation to get current user
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Role> roles = roleRepository.findByName(profile.getRoles());
		User currentUser = userRepository.findByUsername(principal.getUsername()).get();
		
		ProfileDetails profileDetail = new ProfileDetails(profile.getFullName(), profile.getOccupation(), profile.getPan(), profile.getDob(), profile.getMobile(), null);
		User user = new User(profile.getUserName(), passwordEncoder.encode(profile.getPassword()), profile.getEmail(), roles, profileDetail, currentUser.getGroup());
		User result  =  userRepository.save(user);

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/group")
				.buildAndExpand(result).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "User successfully created"));
	}

	@PostMapping("/user/entityuser/")
	@PreAuthorize("hasAuthority('USER') or hasAuthority('GROUP_ADMIN')")
	public ResponseEntity<?> createEntityUser(@Valid @RequestBody EntityUserProfileCreation profile) {
		if(userRepository.existsByEmail(profile.getEmail()) || userRepository.existsByUsername(profile.getUserName())) {
			return new ResponseEntity<>(new ApiResponse(false, "User already exists"), HttpStatus.BAD_REQUEST);
		}
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Role> roles = roleRepository.findByName(profile.getRoles());
		ProfileDetails profileDetail = new ProfileDetails(profile.getFullName(),profile.getOccupation() , profile.getPan(), profile.getDob(), 
				profile.getMobile(), profile.getEntityType());
		User currentUser = userRepository.findByUsername(principal.getUsername()).get();
		User user = new User(profile.getUserName(), passwordEncoder.encode(profile.getPassword()), profile.getEmail(), roles, profileDetail, currentUser.getGroup());
		
		User result = userRepository.save(user);
		UserGroup userGroup = new UserGroup();
		userGroup.setUsername(principal.getUsername());
		userGroup.getUsers().add(result);		
		userGroup.setGroup(currentUser.getGroup());
		userGroupRepository.save(userGroup);
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/group")
				.buildAndExpand(result).toUri();
		return ResponseEntity.created(location).body(new ApiResponse(true, "User successfully created"));
	}
	
	/**
	 * method to update password
	 * @param password
	 * @return
	 */
	@GetMapping("/updatePass")
	public @ResponseBody User updatePassword(String password){
		
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		user.setPassword(passwordEncoder.encode(password));
		return userRepository.save(user);
	}
}
