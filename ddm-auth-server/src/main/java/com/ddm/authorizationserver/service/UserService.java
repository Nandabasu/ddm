package com.ddm.authorizationserver.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ddm.authorizationserver.exception.RecordNotFoundException;
import com.ddm.authorizationserver.model.Entities;
import com.ddm.authorizationserver.model.Permission;
import com.ddm.authorizationserver.model.Role;
import com.ddm.authorizationserver.model.User;
import com.ddm.authorizationserver.repository.UserDetailRepository;
import com.ddm.authorizationserver.response.EntitiesUserResponse;
import com.ddm.authorizationserver.response.EntityResponse;
import com.ddm.authorizationserver.response.GroupResponse;
import com.ddm.authorizationserver.response.UserResponse;

@Service
public class UserService {

	@Autowired
	private UserDetailRepository userRepository;

	public UserResponse buildUserReponse(User userEntity) {
		UserResponse userResponse = buildFinalUserResponse(userEntity);
		return userResponse;
	}

	public List<EntityResponse> buildEntityResponse(List<Entities> entities) {
		List<EntityResponse> entityResponse = new ArrayList<EntityResponse>();
		for (Entities entity : entities) {
			EntitiesUserResponse user = buildFinalEntityResponse(entity.getUser());
			entityResponse.add(new EntityResponse(entity.getId(), entity.getName(), entity.getType(), user));
		}
		return entityResponse;
	}

	public EntityResponse buildEntityResponse(Entities entity) {
		EntitiesUserResponse user = buildFinalEntityResponse(entity.getUser());
		return new EntityResponse(entity.getId(), entity.getName(), entity.getType(), user);
	}

	public EntitiesUserResponse buildFinalEntityResponse(User user) {
		EntitiesUserResponse eUserResponse = new EntitiesUserResponse(user.getUsername(), user.getEmail(),
				user.getFullName(), user.getOccupation());
		return eUserResponse;
	}

	public List<UserResponse> buildUserReponse(List<User> userEntityList) {
		List<UserResponse> userResponseList = new ArrayList<>();
		for (User userEntity : userEntityList) {
			UserResponse userResponse = buildFinalUserResponse(userEntity);
			userResponseList.add(userResponse);
		}
		return userResponseList;
	}

	private UserResponse buildFinalUserResponse(User userEntity) {
		List<String> roles = new ArrayList<String>();
		List<String> permissions = new ArrayList<String>();
		for (Role role : userEntity.getRoles()) {
			roles.add(role.getName());
			for (Permission permission : role.getPermissions()) {
				permissions.add(permission.getName());
			}
		}
		GroupResponse groupResponse = null;
		if (userEntity.getGroup() != null) {
			groupResponse = new GroupResponse.GroupResponseBuilder(userEntity.getGroup().getId(),
					userEntity.getGroup().getName(), userEntity.getGroup().getDescription()).build();
		}
		UserResponse userResponse = new UserResponse.UserResponseBuilder(userEntity.getId(), userEntity.getUsername(),
				userEntity.getEmail(), userEntity.getFullName(), userEntity.getOccupation(), userEntity.getPan(),
				userEntity.getDob(), userEntity.getMobile(), roles, permissions).setGroup(groupResponse).build();
		return userResponse;
	}

	public List<UserResponse> getAllGroupAdmins() {
		List<User> userEntities = userRepository.getAllGroupAdmins();
		return buildUserReponse(userEntities);
	}

	public UserResponse getUserById(long id) throws RecordNotFoundException {
		Optional<User> userEntity = userRepository.findById(id);

		if (userEntity.isPresent()) {
			return buildUserReponse(userEntity.get());
		} else {
			throw new RecordNotFoundException("user does not exist!");
		}

	}

	public User getCurrentLoggedInUser() {
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User currentUser = userRepository.findByUsername(principal.getUsername()).get();
		return currentUser;
	}

	public List<UserResponse> getAllUsersByGroupId(long groupId) {
		List<User> userEntities = userRepository.getAllUsersByGroupId(groupId);
		return buildUserReponse(userEntities);
	}

	public List<UserResponse> getAllUsersByGroupAdminId(long groupAdminId) throws RecordNotFoundException {
		Optional<User> userEntity = userRepository.findById(groupAdminId);

		if (userEntity.isPresent()) {
			long groupId = userEntity.get().getGroup().getId();
			return getAllUsersByGroupId(groupId);

		} else {
			throw new RecordNotFoundException("Group admin does not exist!");
		}
	}

	/*
	 * public UserResponse getEntityById(long id) throws RecordNotFoundException {
	 * Optional<User> userEntity = userRepository.getEntityById(id);
	 * 
	 * if (userEntity.isPresent()) { System.out.println("sari"); return
	 * buildUserReponse(userEntity.get()); } else { throw new
	 * RecordNotFoundException("Entity does not exist!"); } }
	 */
}
