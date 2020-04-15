package com.ddm.authorizationserver.controller;

import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ddm.authorizationserver.exception.RecordNotFoundException;
import com.ddm.authorizationserver.exception.ResourceNotFoundException;
import com.ddm.authorizationserver.model.Group;
import com.ddm.authorizationserver.model.Role;
import com.ddm.authorizationserver.model.User;
import com.ddm.authorizationserver.payload.ApiResponse;
import com.ddm.authorizationserver.payload.ErrorResponse;
import com.ddm.authorizationserver.repository.GroupRepository;
import com.ddm.authorizationserver.repository.RoleRepository;
import com.ddm.authorizationserver.repository.UserDetailRepository;
import com.ddm.authorizationserver.request.UserRequest;
import com.ddm.authorizationserver.response.UserResponse;
import com.ddm.authorizationserver.service.UserService;

@RestController
@RequestMapping(value = "v1/groups/admin")
public class GroupAdminController {

	private static final Logger logger = LoggerFactory.getLogger(GroupAdminController.class);

	@Autowired
	private UserDetailRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserService userService;

	/**
	 * method to create group_admin, this is be only be allowed for Master Admin
	 * role
	 * 
	 * @return
	 */
	@PostMapping
	@PreAuthorize("hasAuthority('MASTER_ADMIN')")
	public ResponseEntity<?> createGroupAdmin(@Valid @RequestBody UserRequest profile) {

		logger.info("create group admin by Master Admin.... createGroupAdmin");
		if (userRepository.existsByEmail(profile.getEmail()) || userRepository.existsByUsername(profile.getUserName())
				|| userRepository.existsByPan(profile.getPan()) || userRepository.existsByMobile(profile.getMobile())) {
			return new ResponseEntity<>(new ApiResponse(false, "User already exists"), HttpStatus.BAD_REQUEST);
		}
		if (groupRepository.existsByName(profile.getUserName())) {
			return new ResponseEntity<>(new ApiResponse(false, "Group already exists"), HttpStatus.BAD_REQUEST);
		}
		Set<Role> roles = roleRepository.findByName(profile.getRoles());
		Group group = new Group(profile.getUserName(), profile.getFullName().concat(profile.getUserName()));
		Group groupResult = groupRepository.save(group);
		User user = new User(profile.getUserName(), passwordEncoder.encode(profile.getPassword()), profile.getEmail(),
				profile.getFullName(), profile.getOccupation(), profile.getPan(), profile.getDob(), profile.getMobile(),
				profile.getIsEnterprise(), true, true, true, true, roles, groupResult);
		User result = userRepository.save(user);
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("v1/groups/admin")
				.buildAndExpand(result).toUri();
		return ResponseEntity.created(location).body(new ApiResponse(true, "Group admin successfully created"));
	}

	@GetMapping
	@PreAuthorize("hasAuthority('MASTER_ADMIN')")
	public ResponseEntity<List<UserResponse>> getAllGroupAdmins() {

		return new ResponseEntity<List<UserResponse>>(userService.getAllGroupAdmins(), new HttpHeaders(),
				HttpStatus.OK);
	}

	@GetMapping(value = "/{id}")
	@PreAuthorize("hasAuthority('MASTER_ADMIN') or hasAuthority('GROUP_ADMIN')")
	public ResponseEntity<?> getGroupAdminById(@PathVariable long id) throws RecordNotFoundException {

		User loggedInUser = userService.getCurrentLoggedInUser();
		if (loggedInUser.getId() == id
				|| loggedInUser.getRoles().stream().anyMatch(role -> "MASTER_ADMIN".equalsIgnoreCase(role.getName()))) {
			return new ResponseEntity<UserResponse>(userService.getUserById(id), new HttpHeaders(), HttpStatus.OK);
		}
		return new ResponseEntity<>(new ErrorResponse("403", "Access denied"), HttpStatus.FORBIDDEN);

	}

	@DeleteMapping(value = "/{id}")
	@PreAuthorize("hasAuthority('MASTER_ADMIN')")
	public ResponseEntity<?> deleteGroupAdminById(@PathVariable("id") long id) {
		return userRepository.findById(id).map(user -> {
			userRepository.delete(user);
			return ResponseEntity.ok().body(new ApiResponse(true, "Group admin deleted successfully"));
		}).orElseThrow(() -> new ResourceNotFoundException("UserId " + id + " not found"));

	}

	@GetMapping(value = "/{id}/users")
	@PreAuthorize("hasAuthority('MASTER_ADMIN') or hasAuthority('GROUP_ADMIN')")
	public ResponseEntity<?> getAllUsersByGroupAdminId(@PathVariable long id) throws RecordNotFoundException {
		List<UserResponse> userResponseList = userService.getAllUsersByGroupAdminId(id);
		return new ResponseEntity<List<UserResponse>>(userResponseList, new HttpHeaders(), HttpStatus.OK);
	}

}
