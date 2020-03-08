package com.ddm.authorizationserver.service;
/*package com.krishantha.rentcloud.authorizationserver.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.krishantha.rentcloud.authorizationserver.model.Group;
import com.krishantha.rentcloud.authorizationserver.model.Role;
import com.krishantha.rentcloud.authorizationserver.model.User;
import com.krishantha.rentcloud.authorizationserver.payload.ApiResponse;
import com.krishantha.rentcloud.authorizationserver.payload.UserRegistrationRequestPayload;
import com.krishantha.rentcloud.authorizationserver.repository.GroupRepository;
import com.krishantha.rentcloud.authorizationserver.repository.RoleRepository;
import com.krishantha.rentcloud.authorizationserver.repository.UserDetailRepository;

@Service
public class UserProfileService {

	@Autowired
	UserDetailRepository userRepository;
	
	
	@Autowired
	GroupRepository groupRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
    @Autowired
    private PasswordEncoder passwordEncoder;

	public ResponseEntity<?> registerUser(UserRegistrationRequestPayload userPayload){
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if(userRepository.existsByEmail(userPayload.getEmail()) || userRepository.existsByUsername(userPayload.getUserName())) {
			return new ResponseEntity<>(new ApiResponse(false, "User already exists"), HttpStatus.BAD_REQUEST);
		}
		
		boolean addToGroup = user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_admin"));
		if(addToGroup) {
			Group group = new Group();
			group.setName(userPayload.getUserName());
			groupRepository.save(group);
		}
		
		User newUser = new User();
		newUser.setAccountNonExpired(true);
		newUser.setAccountNonLocked(true);
		newUser.setCredentialsNonExpired(true);
		newUser.setEnabled(true);
		newUser.setDob(userPayload.getDob());
		newUser.setEmail(userPayload.getEmail());
		newUser.setFullName(userPayload.getFullName());
		newUser.setMobile(userPayload.getMobile());
		newUser.setOccupation(userPayload.getOccupation());
		newUser.setPan(userPayload.getPan());
		newUser.setPassword(passwordEncoder.encode(userPayload.getPassword()));
		newUser.setUsername(userPayload.getUserName());
		
		List<Role> roles = roleRepository.findByName(userPayload.getRoles());
		newUser.setRoles(roles);
		User result = userRepository.save(newUser);

		return ResponseEntity.created().body(new ApiResponse(true, "User successfully created"));	
		return result;
	}
}
*/