package com.ddm.authorizationserver.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ddm.authorizationserver.exception.ResourceNotFoundException;
import com.ddm.authorizationserver.model.Group;
import com.ddm.authorizationserver.repository.GroupRepository;
import com.ddm.authorizationserver.request.GroupRequest;
import com.ddm.authorizationserver.response.ApiResponse;

@RequestMapping("/api/v1/groups")
@RestController
public class GroupController {
	
	@Autowired
	GroupRepository groupRepository;
	
	@GetMapping("/")
	@ResponseBody
	@PreAuthorize("hasAuthority('MASTER_ADMIN')")
	public List<Group> getAllGroups(){
		return	groupRepository.findAll();
	}
	
	@GetMapping(value = "/{id}")
	@PreAuthorize("hasAuthority('MASTER_ADMIN') or hasAuthority('GROUP_ADMIN')")
	public ResponseEntity<?> getGroupById(@PathVariable long id){
		
		if(!groupRepository.existsById(id)) {
			return new ResponseEntity<>(new ApiResponse(false, "Group does not exists"), HttpStatus.BAD_REQUEST);
		}
		
		Group result = groupRepository.findById(id).get();		
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/groups")
			.buildAndExpand(result).toUri();
		
		return ResponseEntity.created(location).body(result);
	}
 
	@PostMapping("/save")
	@PreAuthorize("hasAuthority('MASTER_ADMIN')")
	public ResponseEntity<?> createGroup(@Valid @RequestBody GroupRequest group) {
		if(groupRepository.existsByName(group.getName())) {
			return new ResponseEntity<>(new ApiResponse(false, "Group name already exists"), HttpStatus.BAD_REQUEST);
		}
		Group groupObj = new Group(group.getName(), group.getDescription());
		Group result  = groupRepository.save(groupObj);
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/groups")
				.buildAndExpand(result).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "Group Created Successfully"));
	}
	
	@PutMapping("/update")
	@PreAuthorize("hasAuthority('MASTER_ADMIN')")
	public Group updateGroup(@Valid @RequestBody GroupRequest group) {
        return groupRepository.findById(group.getId()).map(group1 -> {
            group1.setName(group.getName());
            group1.setDescription(group.getDescription());
            return groupRepository.save(group1);
        }).orElseThrow(() -> new ResourceNotFoundException("Group ID " + group.getId() + "not found"));
	}
	
	@DeleteMapping(value = "/{id}")
	@PreAuthorize("hasAuthority('MASTER_ADMIN')")
	public ResponseEntity<?> deleteGroup(@PathVariable("id") long id){
		if(!groupRepository.existsById(id)) {
			return new ResponseEntity<>(new ApiResponse(false, "Group does not exists"), HttpStatus.BAD_REQUEST);
		}
		groupRepository.deleteById(id);
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/groups")
				.buildAndExpand(id).toUri();
		return ResponseEntity.created(location).body(new ApiResponse(true, "Group Deleted Successfully"));
	}
}
