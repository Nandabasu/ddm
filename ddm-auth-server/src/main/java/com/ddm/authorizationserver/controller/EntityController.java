package com.ddm.authorizationserver.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.ddm.authorizationserver.model.Entities;
import com.ddm.authorizationserver.payload.ApiResponse;
import com.ddm.authorizationserver.payload.EntityPayload;
import com.ddm.authorizationserver.repository.EntitiesRepository;
import com.ddm.authorizationserver.repository.UserDetailRepository;
import com.ddm.authorizationserver.response.EntityResponse;
import com.ddm.authorizationserver.service.UserService;

@RestController
@RequestMapping(value = "v1/entities")
public class EntityController {
	private static final Logger logger = LoggerFactory.getLogger(GroupAdminController.class);

	@Autowired
	EntitiesRepository entitiesRepository;

	@Autowired
	UserDetailRepository userRepository;
	
	@Autowired
	UserService userService;
	
	@GetMapping
	public List<EntityResponse> getAllEntities(){
		logger.info("getAllEntities() method has been called......"); 
		return userService.buildEntityResponse(entitiesRepository.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getEntity(@PathVariable("id") long id){
		logger.info("getAllEntities() method has been called......"); 
		if(!entitiesRepository.existsById(id)) {
			return new ResponseEntity<>(new ApiResponse(false, "Entity Name already exists, please choose different name"), HttpStatus.BAD_REQUEST);
		}
		Entities result = entitiesRepository.findById(id).get();
		return ResponseEntity.ok(result);
	}

	@PostMapping
	public ResponseEntity<?> createEntity(@Valid @RequestBody EntityPayload entityPayload){
		logger.info("createEntity() method called"); 
		if(entitiesRepository.existsByName(entityPayload.getEntityName())) {
			return new ResponseEntity<>(new ApiResponse(false, "Entity Name already exists, please choose different name"), HttpStatus.BAD_REQUEST);
		}
		Entities entity = new Entities(entityPayload.getEntityName(), entityPayload.getEntityType());
		entity.setUser(userRepository.findById(entityPayload.getUserId()).get());
		Entities result = entitiesRepository.save(entity);
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("v1/entities").buildAndExpand(result)
				.toUri();
		return ResponseEntity.created(location).body(new ApiResponse(true, "Entity successfully created"));
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> deleteEntity(@PathVariable("id") long id){
		logger.info("deleteEntity() method called"); 
		if(!entitiesRepository.existsById(id)) {
			return new ResponseEntity<>(new ApiResponse(false, "Entity does not exists"), HttpStatus.BAD_REQUEST);
		}
		entitiesRepository.deleteById(id);
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("v1/entities")
				.buildAndExpand(id).toUri();
		return ResponseEntity.created(location).body(new ApiResponse(true, "Entity Deleted Successfully"));
	}
	
	@PutMapping("/{id}")
	public EntityResponse updateEntity(@PathVariable("id") long id, @Valid @RequestBody EntityPayload entity) {
		logger.info("updateEntity() method called"); 
        return entitiesRepository.findById(id).map(entity1 -> {
        	entity1.setName(entity.getEntityName());
        	entity1.setType(entity.getEntityType());
        	return userService.buildEntityResponse(entitiesRepository.save(entity1));
        }).orElseThrow(() -> new ResourceNotFoundException("Entity ID " + id + " not found"));
	}
	
}
