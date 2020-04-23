package com.ddm.authorizationserver.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ddm.authorizationserver.exception.ResourceNotFoundException;
import com.ddm.authorizationserver.model.User;
import com.ddm.authorizationserver.repository.UserDetailRepository;
import com.ddm.authorizationserver.request.UpdateUserRequest;
import com.ddm.authorizationserver.response.ApiResponse;
import com.ddm.authorizationserver.response.UserResponse;
import com.ddm.authorizationserver.service.UserService;

@RestController
@RequestMapping(value ="/api/v1/profile")
public class UserProfileController {

	private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

	@Autowired
	private UserDetailRepository userRepository;
	
	@Autowired
	private UserService userService;

	@GetMapping(value = "/")
	@PreAuthorize("hasAuthority('MASTER_ADMIN')")
	public List<UserResponse> getAllUsers() {
		List<User> userEntityList=userRepository.findAll();
		return userService.buildUserReponse(userEntityList);
	}

	@GetMapping(value = "/user/{id}")
	@PreAuthorize("hasAuthority('MASTER_ADMIN') or hasAuthority('GROUP_ADMIN')")
	public ResponseEntity<?> getUserById(@PathVariable long id) {
		if (!userRepository.existsById(id)) {
			return new ResponseEntity<>(new ApiResponse(false, "User does not exists"), HttpStatus.BAD_REQUEST);
		}		
		User user = userRepository.findById(id).get();
		UserResponse userResponse = userService.buildUserReponse(user);
		return ResponseEntity.ok().body(userResponse);
	}

	@DeleteMapping(value = "/user/{id}")
	@PreAuthorize("hasAuthority('MASTER_ADMIN')")
	public ResponseEntity<?> deleteUserById(@PathVariable long id){
		if(!userRepository.existsById(id)) {
			return new ResponseEntity<>(new ApiResponse(false, "User does not exists"), HttpStatus.BAD_REQUEST);
		}
		User userObj = userRepository.findById(id).get();
		userRepository.delete(userObj);
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/")
				.buildAndExpand(id).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "User deleted successfully"));
	}
	
	@PutMapping(value = "/user")
	public UserResponse updateUser(@Valid @RequestBody UpdateUserRequest userPayload){
		logger.info("Update user method:");
        return userRepository.findById(userPayload.getId()).map(user -> {
        	user.setOccupation(userPayload.getOccupation());
            User userResponse = userRepository.save(user);
            return userService.buildUserReponse(userResponse);
        }).orElseThrow(() -> new ResourceNotFoundException("User ID " + userPayload.getId() + "not found"));
	}
}
