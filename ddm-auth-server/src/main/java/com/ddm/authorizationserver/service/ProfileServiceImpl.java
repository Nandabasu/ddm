/*package com.ddm.authorizationserver.service;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ddm.authorizationserver.model.Group;
import com.ddm.authorizationserver.model.Role;
import com.ddm.authorizationserver.model.User;
import com.ddm.authorizationserver.payload.ProfileCreationResponse;
import com.ddm.authorizationserver.payload.UserRegistrationRequestPayload;
import com.ddm.authorizationserver.repository.GroupRepository;
import com.ddm.authorizationserver.repository.RoleRepository;
import com.ddm.authorizationserver.repository.UserDetailRepository;

@Service
public class ProfileServiceImpl implements ProfileService{

	private static final Logger logger = LoggerFactory.getLogger(ProfileServiceImpl.class);
	@Autowired
	UserDetailRepository userRepository;
	
	@Autowired
	GroupRepository groupRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
    @Autowired
    private PasswordEncoder passwordEncoder;

    
	@Override
	public ProfileCreationResponse createUser(UserRegistrationRequestPayload payload) throws SQLException {
		
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		boolean createGroup = user.getRoles().stream().anyMatch(role -> role.getName().equals("MASTER_ADMIN")) && payload.getRoles().contains("GROUP_ADMIN") ? true : false; 
		try {

			if(userRepository.existsByEmail(payload.getEmail()) || userRepository.existsByUsername(payload.getUserName())) {
				ProfileCreationResponse profileResponse = new ProfileCreationResponse();
				profileResponse.setMessage("User already exists");
				profileResponse.setProfile(null);
				profileResponse.setStatusCode(HttpStatus.CONFLICT);
				profileResponse.setSuccess(false);
				return profileResponse;
			}
					
			User newUser = new User();
			newUser.setDob(payload.getDob());
			newUser.setEmail(payload.getEmail());
			newUser.setFullName(payload.getFullName());
			newUser.setMobile(payload.getMobile());
			newUser.setOccupation(payload.getOccupation());
			newUser.setPan(payload.getPan());
			newUser.setPassword(passwordEncoder.encode(payload.getPassword()));
			newUser.setUsername(payload.getUserName());
			
			List<Role> roles = roleRepository.findByName(payload.getRoles());
			newUser.setRoles(roles);
			User completeUser = userRepository.findByUsername(user.getUsername()).get();
			if(createGroup) {
				Group group = new Group();
				group.setName(payload.getUserName());
				group.getUser().add(newUser);
				newUser.setGroup(group);
				groupRepository.save(group);			
			}else {
				newUser.setGroup(completeUser.getGroup());
			}
//			newUser.setGroup(group);
			User result = userRepository.save(newUser);

			ProfileCreationResponse profileResponse = new ProfileCreationResponse();
			profileResponse.setMessage("User created successfully");
	//		profileResponse.setProfile(formResponseProfile(result));
			profileResponse.setStatusCode(HttpStatus.CREATED);
			profileResponse.setSuccess(true);
			return profileResponse;	
			
		}catch (DataAccessException e) {
			logger.error("Exception occurred while profile creation: "+e);
		}
		return null;
	}
	
	public static Profile formResponseProfile(User user) {
		Profile profile = new Profile(user.getUsername(), user.getEmail(), user.getFullName(),
				user.getOccupation(), user.getPan(), user.getDob(), 
				user.getMobile(), user.getRoles(), null, user.getGroup());
		return profile;
	}
}
*/