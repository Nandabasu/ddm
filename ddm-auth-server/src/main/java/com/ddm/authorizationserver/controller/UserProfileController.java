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

import com.ddm.authorizationserver.model.Group;
import com.ddm.authorizationserver.model.ProfileDetails;
import com.ddm.authorizationserver.model.Role;
import com.ddm.authorizationserver.model.User;
import com.ddm.authorizationserver.model.UserGroup;
import com.ddm.authorizationserver.payload.ApiResponse;
import com.ddm.authorizationserver.payload.CommonUserInfo;
import com.ddm.authorizationserver.payload.ProfileCreation;
import com.ddm.authorizationserver.payload.ProfileCreationResponse;
import com.ddm.authorizationserver.payload.UserRegistrationRequestPayload;
import com.ddm.authorizationserver.repository.GroupRepository;
import com.ddm.authorizationserver.repository.RoleRepository;
import com.ddm.authorizationserver.repository.UserDetailRepository;
import com.ddm.authorizationserver.repository.UserGroupRepository;

@RestController
@RequestMapping(value ="/api/v1/")
public class UserProfileController {

	private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);
    
	/*@Autowired
	ProfileServiceImpl profileService;*/
	
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
    
    @GetMapping("/secured")
//    @Secured("MASTER_ADMIN")
    @PreAuthorize("hasAuthority('MASTER_ADMIN') or hasAuthority('GROUP_ADMIN')")
 //   @PreAuthorize("hasRole('USER')")
    public String test_method_level_security() {
    	
    	return "This is what you earned..........";
    }
    
    
	@PostMapping(value="/create_user")
    @PreAuthorize("hasAuthority('MASTER_ADMIN') or hasAuthority('GROUP_ADMIN')")
	public @ResponseBody ResponseEntity<Object> createUser(@RequestBody UserRegistrationRequestPayload payload){
		logger.info("Starts at createUser() ");
		ProfileCreationResponse profile = null;
		try {
		//	 profile =  profileService.createUser(payload);
			URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/create_user")
					.buildAndExpand(profile.getProfile().getUsername()).toUri();

			return ResponseEntity.created(location).body(profile);	
		}catch (Exception e) {
			logger.error("Exception occurred while creating user.... "+e);
		}
		return new ResponseEntity<Object>(profile, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * method to create group_admin, this is be only be allowed for Master Admin role
	 * @return
	 */
	@PostMapping("/user/")
	@PreAuthorize("hasAuthority('MASTER_ADMIN')")
	public ResponseEntity<?> createGroupAdmin(@Valid @RequestBody ProfileCreation profile) {
		
		if(userRepository.existsByEmail(profile.getCommonUserInfo().getEmail()) || userRepository.existsByUsername(profile.getCommonUserInfo().getUserName())) {
			return new ResponseEntity<>(new ApiResponse(false, "User already exists"), HttpStatus.BAD_REQUEST);
		}
		
		CommonUserInfo userInfo = profile.getCommonUserInfo();
		List<Role> roles = roleRepository.findByName(profile.getRoles());
		
		ProfileDetails profileDetail = new ProfileDetails(userInfo.getFullName(),userInfo.getOccupation() , userInfo.getPan(), userInfo.getDob(), userInfo.getMobile(), null);
		Group group = new Group(userInfo.getFullName());
		Group groupResult = groupRepository.save(group);
		
		User user = new User(userInfo.getUserName(), passwordEncoder.encode(userInfo.getPassword()), userInfo.getEmail(), roles, profileDetail, groupResult);
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
		if(userRepository.existsByEmail(profile.getCommonUserInfo().getEmail()) || userRepository.existsByUsername(profile.getCommonUserInfo().getUserName())) {
			return new ResponseEntity<>(new ApiResponse(false, "User already exists"), HttpStatus.BAD_REQUEST);
		}
		// This should be replaced to annotation to get current user
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		CommonUserInfo userInfo = profile.getCommonUserInfo();
		List<Role> roles = roleRepository.findByName(profile.getRoles());
		User currentUser = userRepository.findByUsername(principal.getUsername()).get();
		
		ProfileDetails profileDetail = new ProfileDetails(userInfo.getFullName(),userInfo.getOccupation() , userInfo.getPan(), userInfo.getDob(), userInfo.getMobile(), null);
		User user = new User(userInfo.getUserName(), passwordEncoder.encode(userInfo.getPassword()), userInfo.getEmail(), roles, profileDetail, currentUser.getGroup());
		User result  =  userRepository.save(user);

		/*Group group = currentUser.getGroup();
		Set<User> userList = result.getGroup().getUser();
		userList.add(result);
		group.setUser(userList);
		groupRepository.save(group);*/
		
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/group")
				.buildAndExpand(result).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "User successfully created"));
	}

	@PostMapping("/user/entityuser/")
	@PreAuthorize("hasAuthority('USER') or hasAuthority('GROUP_ADMIN')")
	public ResponseEntity<?> createEntityUser(@Valid @RequestBody ProfileCreation profile) {
		if(userRepository.existsByEmail(profile.getCommonUserInfo().getEmail()) || userRepository.existsByUsername(profile.getCommonUserInfo().getUserName())) {
			return new ResponseEntity<>(new ApiResponse(false, "User already exists"), HttpStatus.BAD_REQUEST);
		}
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		CommonUserInfo userInfo = profile.getCommonUserInfo();
		List<Role> roles = roleRepository.findByName(profile.getRoles());
//		EntityUserType entityType = entityUserRepository.findByEntityType(profile.getEntityType()).get();
		ProfileDetails profileDetail = new ProfileDetails(userInfo.getFullName(),userInfo.getOccupation() , userInfo.getPan(), userInfo.getDob(), 
				userInfo.getMobile(), profile.getEntityType());
		User currentUser = userRepository.findByUsername(principal.getUsername()).get();
		User user = new User(userInfo.getUserName(), passwordEncoder.encode(userInfo.getPassword()), userInfo.getEmail(), roles, profileDetail, currentUser.getGroup());
		
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
