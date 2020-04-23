package com.ddm.authorizationserver.controller;

import java.net.URI;
import java.security.Principal;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ddm.authorizationserver.exception.DdmException;
import com.ddm.authorizationserver.exception.RecordNotFoundException;
import com.ddm.authorizationserver.exception.ResourceNotFoundException;
import com.ddm.authorizationserver.model.Group;
import com.ddm.authorizationserver.model.Role;
import com.ddm.authorizationserver.model.User;
import com.ddm.authorizationserver.repository.UserDetailRepository;
import com.ddm.authorizationserver.request.UserRequest;
import com.ddm.authorizationserver.response.ApiResponse;
import com.ddm.authorizationserver.response.UserResponse;
import com.ddm.authorizationserver.service.UserService;

@RestController
@RequestMapping(value = "v1/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserDetailRepository userRepository;

    @Autowired
    private UserService userService;


    /**
     * method to create group_admin, this is be only be allowed for Master Admin role
     *
     * @return
     */
    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_USER')")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequest profile) throws DdmException {
        User user = userService.createUserBasedOnRole(profile);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/").buildAndExpand(user).toUri();
        return ResponseEntity.created(location).body(new ApiResponse(true, "User successfully created"));
    }


    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('MASTER_ADMIN') or hasAuthority('GROUP_ADMIN') or hasAuthority('USER')")
    public ResponseEntity<?> getUserById(@PathVariable long id, Principal principal) throws RecordNotFoundException, DdmException {
        return ResponseEntity.ok().body(userService.getUserById(id));
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('MASTER_ADMIN') or hasAuthority('GROUP_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable("id") long id) {
        return userRepository.findById(id).map(user -> {
            userRepository.delete(user);
            return ResponseEntity.ok().body(new ApiResponse(true, "User deleted successfully"));
        }).orElseThrow(() -> new ResourceNotFoundException("UserId " + id + " not found"));

    }

    @GetMapping("/groupAdmins")
    @PreAuthorize("hasAuthority('MASTER_ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllGroupAdmins() {
        return new ResponseEntity<List<UserResponse>>(userService.getAllGroupAdmins(), new HttpHeaders(),
                HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/users")
    @PreAuthorize("hasAuthority('MASTER_ADMIN') or hasAuthority('GROUP_ADMIN')")
    public ResponseEntity<?> getAllUsersByGroupAdminId(@PathVariable long id) throws RecordNotFoundException, DdmException {
        List<UserResponse> userResponseList = userService.getAllUsersByGroupAdminId(id);
        return new ResponseEntity<List<UserResponse>>(userResponseList, new HttpHeaders(), HttpStatus.OK);
    }
    
    @PostMapping(value = "/signup")
    public ResponseEntity<?> signUp(@RequestBody @Valid UserRequest signupRequest) {
        User user = userService.signUp(signupRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/user/signup").buildAndExpand(user).toUri();
        return ResponseEntity.created(location).body(new ApiResponse(true, "You have successfully signed up"));
    }
}
