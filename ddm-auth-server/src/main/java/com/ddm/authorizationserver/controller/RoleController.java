package com.ddm.authorizationserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ddm.authorizationserver.model.Role;
import com.ddm.authorizationserver.repository.RoleRepository;

@RestController
@RequestMapping("/api/v1/")
public class RoleController {

	@Autowired
	RoleRepository roleRepository;
	
	@GetMapping("/role")
	public @ResponseBody Role getRoles(String roleName){
		
		return roleRepository.findByName(roleName).get();
		
	}

	public @ResponseBody Role addRole(String roleName) {
		Role role = new Role();
		role.setName(roleName);
		return roleRepository.save(role);
	}
}
