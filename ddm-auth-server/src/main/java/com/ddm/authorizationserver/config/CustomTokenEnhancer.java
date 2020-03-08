package com.ddm.authorizationserver.config;

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

import com.ddm.authorizationserver.model.Role;
import com.ddm.authorizationserver.model.User;
import com.ddm.authorizationserver.repository.GroupRepository;
import com.ddm.authorizationserver.repository.RoleRepository;
import com.ddm.authorizationserver.repository.UserDetailRepository;

public class CustomTokenEnhancer extends JwtAccessTokenConverter{

	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private UserDetailRepository userRepository;
	
	@Autowired
	private GroupRepository groupRepository;
	
	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		User user = (User) authentication.getPrincipal();

		Map<String, Object> info = new LinkedHashMap<String, Object>(accessToken.getAdditionalInformation());
		User moreUserInfo = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<String> roles = moreUserInfo.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
		User currentUser = userRepository.findByUsername(moreUserInfo.getUsername()).get();
		
		boolean master_admin = currentUser.getRoles().stream().anyMatch(role -> role.getName().contains("MASTER_ADMIN"));
		boolean group_admin = currentUser.getRoles().stream().anyMatch(role -> role.getName().contains("GROUP_ADMIN"));
		boolean role_user = currentUser.getRoles().stream().anyMatch(role -> role.getName().contains("USER"));
		boolean entity_user = currentUser.getRoles().stream().anyMatch(role -> role.getName().contains("ENTITY_USER"));
		if(master_admin)
				info.put("groups", groupRepository.findAll());
		if(group_admin) {
			List<String> group_users = currentUser.getGroup().getUser().stream().map(User::getUsername).collect(Collectors.toList());
			info.put("users", group_users);
		}
		/*if(group_admin) {
			Set<User> test_group_user = currentUser.getGroup().getUser();
			info.put("Users as Object", test_group_user);
		}*/

		/*if(role_user)
			    currentUser.getGroup().getUser().stream()
						.filter( u -> u.getRoles().contains("ENTITY_USER"))						
						.collect(Collectors.toList());*/
		
		info.put("email", user.getEmail());
		info.put("roles", roles);
		DefaultOAuth2AccessToken customAccessToken = new DefaultOAuth2AccessToken(accessToken);
		customAccessToken.setAdditionalInformation(info);

		return super.enhance(customAccessToken, authentication);
	}
}
