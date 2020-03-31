package com.ddm.authorizationserver.controller;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ddm.authorizationserver.model.EntityUser;
import com.ddm.authorizationserver.model.Group;
import com.ddm.authorizationserver.model.Role;
import com.ddm.authorizationserver.model.User;
import com.ddm.authorizationserver.payload.ApiResponse;
import com.ddm.authorizationserver.payload.EntityUserAccessPayload;
import com.ddm.authorizationserver.payload.ProfileCreation;
import com.ddm.authorizationserver.repository.EntityUserRepository;
import com.ddm.authorizationserver.repository.GroupRepository;
import com.ddm.authorizationserver.repository.RoleRepository;
import com.ddm.authorizationserver.repository.UserDetailRepository;
import com.ddm.authorizationserver.response.UserResponse;
import com.ddm.authorizationserver.service.UserService;

@RestController
@RequestMapping(value ="/api/v1/profile")
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
	private EntityUserRepository entityUserRepo;
	
	@Autowired
	private UserService userService;

	@GetMapping(value = "/")
	public List<UserResponse> getAllUsers() {
		List<User> userEntityList=userRepository.findAll();
		return userService.buildUserReponse(userEntityList);
	}
	/**
	 * method to create group_admin, this is be only be allowed for Master Admin role
	 * @return
	 */
	@PostMapping("/create")
	@PreAuthorize("hasAuthority('MASTER_ADMIN') or hasAuthority('GROUP_ADMIN') or hasAuthority('USER')")
	public ResponseEntity<?> createGroupAdmin(@Valid @RequestBody ProfileCreation profile) {
		logger.info("create user by Master Admin.... createGroupAdmin()");
		if(userRepository.existsByEmail(profile.getEmail()) || userRepository.existsByUsername(profile.getUserName())
			|| userRepository.existsByPan(profile.getPan()) || userRepository.existsByMobile(profile.getMobile())) {
			return new ResponseEntity<>(new ApiResponse(false, "User already exists"), HttpStatus.BAD_REQUEST);
		}
		User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Set<Role> roles = roleRepository.findByName(profile.getRoles());
		User currentUser = userRepository.findByUsername(principal.getUsername()).get();
		User user = null;
		User result = null;
		EntityUser entityUser = null;
		if(principal.getRoles().stream().anyMatch(role -> "MASTER_ADMIN".equalsIgnoreCase(role.getName()))) {
			
			if(groupRepository.existsByName(profile.getUserName())) {
				return new ResponseEntity<>(new ApiResponse(false, "Group already exists"), HttpStatus.BAD_REQUEST);
			}
			Group group = new Group(profile.getUserName(), profile.getFullName().concat(profile.getUserName()));
			Group groupResult = groupRepository.save(group);
			user = new User(profile.getUserName(), passwordEncoder.encode(profile.getPassword()), profile.getEmail(), profile.getFullName(),
					profile.getOccupation(), profile.getPan(), profile.getDob(), profile.getMobile(), null, true, true,
					true, true, false, roles, groupResult);

			result = userRepository.save(user);
		}

		if(principal.getRoles().stream().anyMatch(role -> "GROUP_ADMIN".equalsIgnoreCase(role.getName()))) {
			user = new User(profile.getUserName(), passwordEncoder.encode(profile.getPassword()), profile.getEmail(), profile.getFullName(),
					profile.getOccupation(), profile.getPan(), profile.getDob(), profile.getMobile(), null, true, true,
					true, true, false, roles, currentUser.getGroup());

			result = userRepository.save(user);
			EntityUser eUser = new EntityUser(user.getId(), null);
			entityUserRepo.save(eUser);
		}

		if (principal.getRoles().stream().anyMatch(role -> "USER".equalsIgnoreCase(role.getName()))) {
			user = new User(profile.getUserName(), passwordEncoder.encode(profile.getPassword()), profile.getEmail(),
					profile.getFullName(), profile.getOccupation(), profile.getPan(), profile.getDob(),
					profile.getMobile(), profile.getEntityType(), true, true, true, true, false, roles,
					currentUser.getGroup());
			user.setEntityUser(true);
			Optional<EntityUser> optionalEntityUser = entityUserRepo.findByEntityUserId(currentUser.getId());
			if(optionalEntityUser.isPresent()) {
				user.setEntityUser(optionalEntityUser.get());
			}else {
				entityUser = new EntityUser(currentUser.getId(), null);
				entityUserRepo.save(entityUser);
				user.setEntityUser(entityUser);
			}
			result = userRepository.save(user);

		}
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/")
				.buildAndExpand(result).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "User successfully created"));
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<?> getUserById(@PathVariable long id) {
		List<User> userEntity = null;
		if (!userRepository.existsById(id)) {
			return new ResponseEntity<>(new ApiResponse(false, "User does not exists"), HttpStatus.BAD_REQUEST);
		}
		Optional<EntityUser> optinalEntityUser = entityUserRepo.findByEntityUserId(id) ;
		
		if (optinalEntityUser.isPresent()) {
			userEntity = optinalEntityUser.get().getUserList();
			List<UserResponse> userResponse = userService.buildUserReponse(userEntity);
			return ResponseEntity.ok().body(userResponse);
		} else {
			return new ResponseEntity<>(new ApiResponse(false, "User does not exists"), HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping(value = "/user/{id}")
	@PreAuthorize("hasAuthority('MASTER_ADMIN')")
	public ResponseEntity<?> deleteUserById(@PathVariable long id){
		if(!userRepository.existsById(id)) {
			return new ResponseEntity<>(new ApiResponse(false, "User does not exists"), HttpStatus.BAD_REQUEST);
		}
		User userObj = userRepository.findById(id).get();
		boolean user = userObj.getRoles().stream().anyMatch(role -> "USER".equalsIgnoreCase(role.getName()));
		if(user) {
			EntityUser entityUser =  entityUserRepo.findByEntityUserId(userObj.getId()).get();
			
			entityUserRepo.deleteById(entityUser.getId());
			userRepository.deleteById(id);
		}
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/")
				.buildAndExpand("deleted").toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "User deleted successfully"));
	}
	
	/*@DeleteMapping(value = "/user/{id}")
	@PreAuthorize("hasAuthority('MASTER_ADMIN')")
	public ResponseEntity<?> deleteEntityUserById(@PathVariable long id){
		if(!userRepository.existsById(id)) {
			return new ResponseEntity<>(new ApiResponse(false, "User does not exists"), HttpStatus.BAD_REQUEST);
		}
		userRepository.deleteById(id);
//		entityUserRepo.deleteById(id);
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/")
				.buildAndExpand("deleted").toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "User deleted successfully"));
	}*/
	
	@DeleteMapping(value = "/groupuser/{id}")
	@PreAuthorize("hasAuthority('MASTER_ADMIN')")
	public ResponseEntity<?> deleteUserById1(@PathVariable long id){
		if(!userRepository.existsById(id)) {
			return new ResponseEntity<>(new ApiResponse(false, "User does not exists"), HttpStatus.BAD_REQUEST);
		}
		userRepository.deleteById(id);
	//	entityUserRepo.deleteById(id);
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/")
				.buildAndExpand("deleted").toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "User deleted successfully"));
	}
	/**
	 * TO-DO complete
	 * method to assign entity user to another user
	 * @param profile
	 * @return
	 */
	@PostMapping(value="/assignUser")
	@PreAuthorize("hasAuthority('GROUP_ADMIN')")
	public ResponseEntity<?> assignEntityUser(@Valid @RequestBody EntityUserAccessPayload userAccessPayload) {

		User entityUser = userRepository.findById(userAccessPayload.getEntityUserId()).get();
		User groupUser = userRepository.findById(userAccessPayload.getUserId()).get();

		User result = null;

		groupUser.setEntityUser(entityUser.getEntityUser());
		result = userRepository.save(groupUser);

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/")
				.buildAndExpand(result).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "User assigned successfully"));
	}


}
