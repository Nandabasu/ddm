package com.ddm.authorizationserver.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ddm.authorizationserver.exception.ResourceNotFoundException;
import com.ddm.authorizationserver.model.Permission;
import com.ddm.authorizationserver.repository.PermissionRepository;
import com.ddm.authorizationserver.request.PermissionRequest;
import com.ddm.authorizationserver.response.ApiResponse;

@RestController
@RequestMapping(value = "v1/previllage")
public class PermissionController {

	@Autowired
	PermissionRepository permissionRepository;
	
	@GetMapping
	public List<Permission> getAllPrevillages(){
		return permissionRepository.findAll();
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<?> getById(@PathVariable long id) {
		if(!permissionRepository.existsById(id)) {
			return new ResponseEntity<>(new ApiResponse(false, "Permission ID does not exists"), HttpStatus.BAD_REQUEST);
		}
	    return new ResponseEntity<>(permissionRepository.findById(id).get(), new HttpHeaders(), HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<?> createPermission(@Valid @RequestBody PermissionRequest permissionRequest){
		if(permissionRepository.existsByName(permissionRequest.getName())) {
			return new ResponseEntity<>(new ApiResponse(false, "Permission Name already exists, please choose different name"), HttpStatus.BAD_REQUEST);
		}
		Permission permission = new Permission(permissionRequest.getName(), permissionRequest.getDescription());
		Permission result = permissionRepository.save(permission);
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("v1/permission").buildAndExpand(result)
				.toUri();
		return ResponseEntity.created(location).body(new ApiResponse(true, "Permission successfully created"));
	}
	
	//  TO-DO 
	// 1: Test When permission deleted, permission should be detached from associated users
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> deletePermission(@PathVariable("id") long id){
		if(!permissionRepository.existsById(id)) {
			return new ResponseEntity<>(new ApiResponse(false, "Permission does not exists"), HttpStatus.BAD_REQUEST);
		}
		permissionRepository.deleteById(id);
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("v1/previllage")
				.buildAndExpand(id).toUri();
		return ResponseEntity.created(location).body(new ApiResponse(true, "previllage Deleted Successfully"));
	}
	
	@PutMapping("/{id}")
	public Permission updatePermission(@PathVariable("id") long id, @Valid @RequestBody PermissionRequest permissionRequest) {
        return permissionRepository.findById(id).map(permission -> {
        	new Permission(permission.getName(), permission.getDescription());
        	return permissionRepository.save(permission);
        }).orElseThrow(() -> new ResourceNotFoundException("Permission ID " + id + " not found"));
	}
}
