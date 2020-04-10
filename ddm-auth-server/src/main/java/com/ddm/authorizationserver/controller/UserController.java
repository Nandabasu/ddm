package com.ddm.authorizationserver.controller;

import java.net.URI;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ddm.authorizationserver.model.EntityUser;
import com.ddm.authorizationserver.model.Role;
import com.ddm.authorizationserver.model.User;
import com.ddm.authorizationserver.payload.ApiResponse;
import com.ddm.authorizationserver.repository.EntityUserRepository;
import com.ddm.authorizationserver.repository.RoleRepository;
import com.ddm.authorizationserver.repository.UserDetailRepository;
import com.ddm.authorizationserver.request.UserRequest;

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
	private EntityUserRepository entityUserRepo;

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
		User user = null;
		User result = null;
		if (principal.getRoles().stream().anyMatch(role -> "GROUP_ADMIN".equalsIgnoreCase(role.getName()))) {
			user = new User(profile.getUserName(), passwordEncoder.encode(profile.getPassword()), profile.getEmail(),
					profile.getFullName(), profile.getOccupation(), profile.getPan(), profile.getDob(),
					profile.getMobile(), null, true, true, true, true, false, roles, currentUser.getGroup());

			result = userRepository.save(user);
			EntityUser eUser = new EntityUser(user.getId(), null);
			entityUserRepo.save(eUser);
		}
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("v1/users").buildAndExpand(result)
				.toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "User successfully created"));
	}

}
