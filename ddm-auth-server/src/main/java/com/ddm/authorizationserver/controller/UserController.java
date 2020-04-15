package com.ddm.authorizationserver.controller;

import java.net.URI;
import java.util.Optional;
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
import com.ddm.authorizationserver.repository.GroupRepository;
import com.ddm.authorizationserver.repository.RoleRepository;
import com.ddm.authorizationserver.repository.UserDetailRepository;
import com.ddm.authorizationserver.request.UserRequest;
import com.ddm.authorizationserver.service.UserService;

@RestController
@RequestMapping(value = "v1/users")
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(GroupAdminController.class);

	@Autowired
	private UserDetailRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private UserService userService;

	@PostMapping
	@PreAuthorize("hasAuthority('MASTER_ADMIN') or hasAuthority('GROUP_ADMIN')")
	public ResponseEntity<?> createUser(@Valid @RequestBody UserRequest profile) {
		logger.info("create user by Master Admin.... createUser()");
		if (userRepository.existsByEmail(profile.getEmail()) || userRepository.existsByUsername(profile.getUserName())
				|| userRepository.existsByPan(profile.getPan()) || userRepository.existsByMobile(profile.getMobile())) {
			return new ResponseEntity<>(new ApiResponse(false, "User already exists"), HttpStatus.BAD_REQUEST);
		}
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Set<Role> roles = roleRepository.findByName(profile.getRoles());
		User currentUser = userRepository.findByUsername(principal.getUsername()).get();
		User result = null;
		User user = null;
		boolean groupAdmin = principal.getRoles().stream()
				.anyMatch(role -> "GROUP_ADMIN".equalsIgnoreCase(role.getName()));
		boolean masterAdmin = principal.getRoles().stream()
				.anyMatch(role -> "MASTER_ADMIN".equalsIgnoreCase(role.getName()));
		if (groupAdmin) {
			user = new User(profile.getUserName(), passwordEncoder.encode(profile.getPassword()), profile.getEmail(),
					profile.getFullName(), profile.getOccupation(), profile.getPan(), profile.getDob(),
					profile.getMobile(), profile.getIsEnterprise(), true, true, true, true, roles,
					currentUser.getGroup());
		}
		if (masterAdmin) {
			if (profile.getGroupId() == null) {
				return new ResponseEntity<>(new ApiResponse(false, "Please choose Group to create user..."),
						HttpStatus.BAD_REQUEST);
			}
			if (!groupRepository.existsById(profile.getGroupId())) {
				return new ResponseEntity<>(
						new ApiResponse(false,
								"Group does not exists, kindly choose right group or contact administrator"),
						HttpStatus.BAD_REQUEST);
			}
			Group group = groupRepository.findById(profile.getGroupId()).get();
			user = new User(profile.getUserName(), passwordEncoder.encode(profile.getPassword()), profile.getEmail(),
					profile.getFullName(), profile.getOccupation(), profile.getPan(), profile.getDob(),
					profile.getMobile(), profile.getIsEnterprise(), true, true, true, true, roles, group);
		}
		result = userRepository.save(user);
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("v1/users").buildAndExpand(result)
				.toUri();
		return ResponseEntity.created(location).body(new ApiResponse(true, "User successfully created"));
	}

	@GetMapping(value = "/{id}")
	@PreAuthorize("hasAuthority('MASTER_ADMIN') or hasAuthority('GROUP_ADMIN') or hasAuthority('USER')")
	public ResponseEntity<?> getUserById(@PathVariable long id) throws RecordNotFoundException {
		return ResponseEntity.ok().body(userService.getUserById(id));
	}

	/*
	 * @GetMapping(value = "{id}/entities")
	 * 
	 * @PreAuthorize("hasAuthority('MASTER_ADMIN') or hasAuthority('GROUP_ADMIN') or hasAuthority('USER')"
	 * ) public ResponseEntity<?> getAllEntityUsersByUserId(@PathVariable long id) {
	 * if (!userRepository.existsById(id)) { return new ResponseEntity<>(new
	 * ApiResponse(false, "User does not exists"), HttpStatus.BAD_REQUEST); }
	 * 
	 * Optional<EntityUser> optinalEntityUser =
	 * entityUserRepo.findByEntityUserId(id); if (optinalEntityUser.isPresent()) {
	 * return
	 * ResponseEntity.ok().body(userService.buildUserReponse(optinalEntityUser.get()
	 * .getUserList())); } else { return new ResponseEntity<>(new ApiResponse(false,
	 * "Not having any entity users AON"), HttpStatus.BAD_REQUEST); }
	 * 
	 * }
	 */

	@DeleteMapping(value = "/{id}")
	@PreAuthorize("hasAuthority('MASTER_ADMIN') or hasAuthority('GROUP_ADMIN')")
	public ResponseEntity<?> deleteUser(@PathVariable("id") long id) {
		return userRepository.findById(id).map(user -> {
			userRepository.delete(user);
			return ResponseEntity.ok().body(new ApiResponse(true, "User deleted successfully"));
		}).orElseThrow(() -> new ResourceNotFoundException("UserId " + id + " not found"));

	}

}
