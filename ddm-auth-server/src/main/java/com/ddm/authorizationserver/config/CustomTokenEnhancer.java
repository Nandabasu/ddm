package com.ddm.authorizationserver.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import com.ddm.authorizationserver.model.Group;
import com.ddm.authorizationserver.model.Permission;
import com.ddm.authorizationserver.model.Role;
import com.ddm.authorizationserver.model.User;
import com.ddm.authorizationserver.model.UserGroup;
import com.ddm.authorizationserver.repository.GroupRepository;
import com.ddm.authorizationserver.repository.RoleRepository;
import com.ddm.authorizationserver.repository.UserDetailRepository;
import com.ddm.authorizationserver.repository.UserGroupRepository;
import com.ddm.authorizationserver.util.UserBasicInfo;

public class CustomTokenEnhancer extends JwtAccessTokenConverter{

	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private UserDetailRepository userRepository;
	
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private UserGroupRepository userGroupRepository;
	
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		Map<String, Object> info = new LinkedHashMap<String, Object>(accessToken.getAdditionalInformation());
		User moreUserInfo = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User currentUser = userRepository.findByUsername(moreUserInfo.getUsername()).get();
		
//		boolean master_admin = currentUser.getRoles().stream().anyMatch(role -> role.getName().contains("MASTER_ADMIN"));
//		boolean group_admin = currentUser.getRoles().stream().anyMatch(role -> role.getName().contains("GROUP_ADMIN"));
//		boolean role_user = currentUser.getRoles().stream().anyMatch(role -> role.getName().contains("USER"));
//		boolean entity_user = currentUser.getRoles().stream().anyMatch(role -> role.getName().contains("ENTITY_USER"));
		boolean master_admin = false;
		boolean group_admin = false;
		boolean role_user = false;
		boolean entity_user = false;
		for(Role role: currentUser.getRoles()) {
			master_admin = role.getName().equals("MASTER_ADMIN");
			group_admin = role.getName().contentEquals("GROUP_ADMIN");
			role_user = role.getName().equals("USER");
			entity_user = role.getName().equals("ENTITY_USER");
		}
		if(master_admin) {
			List<Group> groups = groupRepository.findAll();
			Map<String, List<String>> groupMap = new HashMap<String, List<String>>();
			if(!groups.isEmpty()) {
				for( Group group: groups) {
					List<String> ug = group.getUser().stream().map(User::getUsername).collect(Collectors.toList());
					groupMap.put(group.getName(), ug);
				}
			}
//			List<Role> roles = roleRepository.findAll();
//			roles.stream().filter(role -> role.getPermissions()).collect(Collectors.toList());
			/*Map<String, List<Permission>> rolePrevilagesMap = new HashMap<String, List<Permission>>();
			List<Permission> previllages = new ArrayList<Permission>();
			for(Role role: currentUser.getRoles()) {
				rolePrevilagesMap.put(role.getName(), role.getPermissions());
//				previllages.addAll(role.getPermissions());
			}
			info.put("roles & previllages", rolePrevilagesMap);*/
			info.put("groups", groupMap);
			
		}

		if(group_admin) {
			Map<String, Set<UserBasicInfo>> userGroup = new HashMap<String, Set<UserBasicInfo>>();
			Set<UserBasicInfo> setUsers = new HashSet<UserBasicInfo>();
			for(User user: currentUser.getGroup().getUser()) {
				UserBasicInfo userBasicInfo = new UserBasicInfo(user.getUsername(), user.getProfileDetail().getFullName(),user.getEmail(),user.getProfileDetail().getMobile());
				setUsers.add(userBasicInfo);
				
			}
			userGroup.put(currentUser.getGroup().getName(), setUsers);
			info.put("user_group", userGroup);
		}
		
		if(role_user) {
			List<UserGroup>  userGroup = userGroupRepository.findByUsername(currentUser.getUsername());
			List<UserBasicInfo> entityUsers = new ArrayList<UserBasicInfo>();
			if(!userGroup.isEmpty()) {
				for(UserGroup ug: userGroup) {
					for(User user: ug.getUsers()) {
						UserBasicInfo userBasicInfo = new UserBasicInfo(user.getUsername(), user.getProfileDetail().getFullName(),user.getEmail(),user.getProfileDetail().getMobile());						
						entityUsers.add(userBasicInfo);
					}
				}
			}
			info.put("entity users", entityUsers);
		}
		
		if(entity_user) {
			List<UserGroup>  userGroup = userGroupRepository.findByGroup(currentUser.getGroup());
			String groupName = null;
			for(UserGroup ug: userGroup) {
				groupName = ug.getGroup().getName();
			}
			info.put("Group", groupName);	
		}
		DefaultOAuth2AccessToken customAccessToken = new DefaultOAuth2AccessToken(accessToken);
		customAccessToken.setAdditionalInformation(info);
		customAccessToken.setScope(null);
		accessToken =  super.enhance(customAccessToken, authentication);

		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(new HashMap<>());
		return accessToken;
	}
}
	