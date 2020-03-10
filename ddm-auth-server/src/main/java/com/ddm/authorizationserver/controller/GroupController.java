package com.ddm.authorizationserver.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ddm.authorizationserver.model.Group;
import com.ddm.authorizationserver.repository.GroupRepository;

@RequestMapping("/api/v1/groups")
@RestController
public class GroupController {
	
	@Autowired
	GroupRepository groupRepository;
	
	@GetMapping
	@ResponseBody
	public List<Group> getGroups(){
		
		return	groupRepository.findAll();
	}

}
