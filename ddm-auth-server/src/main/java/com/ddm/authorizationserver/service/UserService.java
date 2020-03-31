package com.ddm.authorizationserver.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ddm.authorizationserver.model.Permission;
import com.ddm.authorizationserver.model.Role;
import com.ddm.authorizationserver.model.User;
import com.ddm.authorizationserver.response.GroupResponse;
import com.ddm.authorizationserver.response.UserResponse;

@Service
public class UserService {
	
	public UserResponse buildUserReponse(User userEntity) {
		UserResponse userResponse = buildFinalUserResponse(userEntity);
		return userResponse;
	}

	
	public List<UserResponse> buildUserReponse(List<User> userEntityList) {
		List<UserResponse> userResponseList=new ArrayList<>();
		for(User userEntity:userEntityList) {
			UserResponse userResponse = buildFinalUserResponse(userEntity);
			userResponseList.add(userResponse);
		}
		return userResponseList;
	}

	private UserResponse buildFinalUserResponse(User userEntity) {
		List<String> roles=new ArrayList<String>();
		List<String> permissions=new ArrayList<String>();
		for (Role role : userEntity.getRoles()) {
			roles.add(role.getName());
			for(Permission permission:role.getPermissions()) {
				permissions.add(permission.getName());
			}
		}
		GroupResponse groupResponse  =null;
		if(userEntity.getGroup()!=null) {
			 groupResponse = new GroupResponse.GroupResponseBuilder(userEntity.getGroup().getId(), userEntity.getGroup().getName(), userEntity.getGroup().getDescription()).build();
		}
		UserResponse userResponse = new UserResponse.UserResponseBuilder(userEntity.getUsername(),
				userEntity.getEmail(), userEntity.getFullName(), userEntity.getOccupation(), userEntity.getPan(),
				userEntity.getDob(), userEntity.getMobile(), roles, permissions, userEntity.getEntityType()).
				setGroup(groupResponse).
				build();
		return userResponse;
	}
}
