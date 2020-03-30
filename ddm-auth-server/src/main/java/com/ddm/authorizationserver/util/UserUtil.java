package com.ddm.authorizationserver.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtil {
	public static Object getCurrentLoggedInUser() {
		return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
}
