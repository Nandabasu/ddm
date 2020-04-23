package com.ddm.authorizationserver.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ddm.authorizationserver.model.Role;
import com.ddm.authorizationserver.repository.RoleRepository;
import com.ddm.authorizationserver.request.RoleRequest;
import com.ddm.authorizationserver.response.ApiResponse;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

	@Autowired
	RoleRepository roleRepository;
	
	@GetMapping("/")
	public @ResponseBody List<Role> getAllRoles(){
		return roleRepository.findAll();
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<?> getRoleById(@PathVariable long id){
		
		if(roleRepository.existsById(id)) {
			return new ResponseEntity<>(new ApiResponse(false, "Role does not exists"), HttpStatus.BAD_REQUEST);
		}
		
		Role result = roleRepository.findById(id).get();		
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/roles")
			.buildAndExpand(result).toUri();
		
		return ResponseEntity.created(location).body(result);
	}
	
	@PostMapping("/save")
	public ResponseEntity<?> addRole(@Valid @RequestBody RoleRequest role) {
	
		if(roleRepository.existsByName(role.getRoleName())) {
			return new ResponseEntity<>(new ApiResponse(false, "Role name already exists"), HttpStatus.BAD_REQUEST);
		}
		
		Role roleObj = new Role(role.getRoleName(), role.getPermissions());
		Role result = roleRepository.save(roleObj);
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/roles")
				.buildAndExpand(result).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "Role Added Successfully"));

	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> deleteRole(@PathVariable("id") long id){

		if(!roleRepository.existsById(id)) {
			return new ResponseEntity<>(new ApiResponse(false, "Role does not exists"), HttpStatus.BAD_REQUEST);
		}
		
		roleRepository.deleteById(id);
		
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/roles")
				.buildAndExpand(id).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "Role Deleted Successfully"));
	}
	
}
