package com.ddm.authorizationserver.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import com.ddm.authorizationserver.model.EntityUser;
import com.ddm.authorizationserver.model.Group;
import com.ddm.authorizationserver.model.Role;
import com.ddm.authorizationserver.model.User;
import com.ddm.authorizationserver.repository.EntityUserRepository;
import com.ddm.authorizationserver.repository.GroupRepository;
import com.ddm.authorizationserver.repository.RoleRepository;
import com.ddm.authorizationserver.repository.UserDetailRepository;
import com.ddm.authorizationserver.response.UserResponse;
import com.ddm.authorizationserver.service.UserService;

public class CustomTokenEnhancer extends JwtAccessTokenConverter{

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private UserDetailRepository userRepo;
	
	@Autowired
	private EntityUserRepository entityUserRepo;
	
	@Autowired
	UserService userService;
	
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		Map<String, Object> info = new LinkedHashMap<String, Object>(accessToken.getAdditionalInformation());
		User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User user = userRepo.findByUsername(principal.getUsername()).get();
		UserResponse profile;
		boolean masterAdmin = principal.getRoles().stream().anyMatch(role -> "MASTER_ADMIN".equalsIgnoreCase(role.getName()));
		boolean groupAdmin = principal.getRoles().stream().anyMatch(role -> "GROUP_ADMIN".equalsIgnoreCase(role.getName()));
		boolean groupUser = principal.getRoles().stream().anyMatch(role -> "USER".equalsIgnoreCase(role.getName()));
		boolean entityUser = principal.getRoles().stream().anyMatch(role -> "ENTITY_USER".equalsIgnoreCase(role.getName()));
		// Change user info to user builder
		if(masterAdmin) {
			List<Role> roles = roleRepo.findAll();
			List<Group> groups = groupRepository.findAll();
			info.put("groups", groups.stream().map(g -> g.getName()).collect(Collectors.toList()));
			info.put("roles", roles.stream().map(roleName -> roleName.getName()).collect(Collectors.toList()));
		}
		
		if(groupAdmin) {
			info.put("group_name", user.getGroup().getName());
			//filter out admin user
			info.put("users", user.getGroup().getUser().stream().map(u -> u.getUsername()).collect(Collectors.toList()));
		}
		if(groupUser) {
			 info.put("group_name", user.getGroup().getName());
			 EntityUser eUser = entityUserRepo.findByEntityUserId(user.getId()).get();
			 List<String> entityUsers = eUser.getUserList().stream().map(eu -> eu.getUsername()).collect(Collectors.toList());
			info.put("entityUser",entityUsers);
		}
		if(entityUser) {
			info.put("group_name", user.getGroup().getName());			
		}
		if(masterAdmin) {
			profile = userService.buildUserReponse(user);
//			profile = new UserBasicInfo(user.getUsername(), user.getFullName(), user.getEmail(), user.getMobile(), null, user.getPan(), user.getOccupation(), user.getDob());
		}
		else{
			profile = userService.buildUserReponse(user);
		}
		info.put("profile",profile);
		DefaultOAuth2AccessToken customAccessToken = new DefaultOAuth2AccessToken(accessToken);
		
	//	customAccessToken.setAdditionalInformation(basicInfo);
		accessToken =  super.enhance(customAccessToken, authentication);
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
		return accessToken;
	}
}
	