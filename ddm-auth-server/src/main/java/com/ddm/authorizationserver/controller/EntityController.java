package com.ddm.authorizationserver.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ddm.authorizationserver.constants.GlobalConstants;
import com.ddm.authorizationserver.exception.DdmException;
import com.ddm.authorizationserver.exception.ResourceNotFoundException;
import com.ddm.authorizationserver.model.Entity;
import com.ddm.authorizationserver.model.GlobalParameters;
import com.ddm.authorizationserver.model.User;
import com.ddm.authorizationserver.repository.EntityRepository;
import com.ddm.authorizationserver.repository.GlobalParametersRepository;
import com.ddm.authorizationserver.repository.UserDetailRepository;
import com.ddm.authorizationserver.request.EntityRequest;
import com.ddm.authorizationserver.response.ApiResponse;
import com.ddm.authorizationserver.response.EntityResponse;
import com.ddm.authorizationserver.service.UserService;

@RestController
@RequestMapping(value = "v1/entities")
public class EntityController {
	private static final Logger logger = LoggerFactory.getLogger(EntityController.class);

	@Autowired
	EntityRepository entityRepository;

	@Autowired
	UserDetailRepository userRepository;
	
	@Autowired
	UserService userService;
	
	@Autowired
	GlobalParametersRepository globalParametersRepository;
	
	@GetMapping
	@PreAuthorize("hasAuthority('MASTER_ADMIN')")
	public List<EntityResponse> getAllEntities(){
		logger.info("getAllEntities() method has been called......"); 
		return userService.buildEntityResponse(entityRepository.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getEntity(@PathVariable("id") long id){
		logger.info("getAllEntities() method has been called......"); 
		if(!entityRepository.existsById(id)) {
			return new ResponseEntity<>(new ApiResponse(false, "Entity Name already exists, please choose different name"), HttpStatus.BAD_REQUEST);
		}
	    return new ResponseEntity<EntityResponse>(userService.buildEntityResponse(entityRepository.findById(id).get()), new HttpHeaders(), HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<?> createEntity(@Valid @RequestBody EntityRequest entityRequest){
		logger.info("createEntity() method called"); 
		if(entityRepository.existsByName(entityRequest.getEntityName())) {
			return new ResponseEntity<>(new ApiResponse(false, "Entity Name already exists, please choose different name"), HttpStatus.BAD_REQUEST);
		}
		Entity entity = new Entity(entityRequest.getEntityName(), entityRequest.getEntityType());
		if(!userRepository.existsById(entityRequest.getUserId())) {
			return new ResponseEntity<>(new ApiResponse(false, "User does not exists"), HttpStatus.BAD_REQUEST);
		}
		User user = userRepository.findById(entityRequest.getUserId()).get();		
		GlobalParameters param = globalParametersRepository.findByParameter(GlobalConstants.ENTITY_LIMIT).get();
		if(user.getEntities().size() == param.getValue() || user.getEntities().size() >= param.getValue()) {
			return new ResponseEntity<>(new ApiResponse(false, "Your entities creation limit is over. Kindly contact administartor"), HttpStatus.BAD_REQUEST);
		}
		entity.setUser(userRepository.findById(entityRequest.getUserId()).get());		
		Entity result = entityRepository.save(entity);
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("v1/entities").buildAndExpand(result)
				.toUri();
		return ResponseEntity.created(location).body(new ApiResponse(true, "Entity successfully created"));
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> deleteEntity(@PathVariable("id") long id){
		logger.info("deleteEntity() method called"); 
		if(!entityRepository.existsById(id)) {
			return new ResponseEntity<>(new ApiResponse(false, "Entity does not exists"), HttpStatus.BAD_REQUEST);
		}
		entityRepository.deleteById(id);
		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("v1/entities")
				.buildAndExpand(id).toUri();
		return ResponseEntity.created(location).body(new ApiResponse(true, "Entity Deleted Successfully"));
	}
	
	@PutMapping("/{id}")
	public EntityResponse updateEntity(@PathVariable("id") long id, @Valid @RequestBody EntityRequest entity) {
		logger.info("updateEntity() method called"); 
        return entityRepository.findById(id).map(entity1 -> {
        	entity1.setName(entity.getEntityName());
        	entity1.setType(entity.getEntityType());
        	return userService.buildEntityResponse(entityRepository.save(entity1));
        }).orElseThrow(() -> new ResourceNotFoundException("Entity ID " + id + " not found"));
	}
	

    @GetMapping(value = "/user/{id}")
    @PreAuthorize("hasAuthority('MASTER_ADMIN') or hasAuthority('GROUP_ADMIN')")
    public List<EntityResponse> getEntitiesByUserId(@PathVariable long id){
    	List<Entity> entities = entityRepository.findByUserId(id);
    	return userService.buildEntities(entities);
    }
    
    @GetMapping(value = "/{id}/entity/{entityName}")
    public List<EntityResponse> getEntityByUserIdAndEntityName(@PathVariable("id") long userId, @PathVariable("entityName") String entityName) throws DdmException{
   
    	if (!userRepository.existsById(userId)) {
            throw new DdmException("400", "User does not exists", HttpStatus.BAD_REQUEST);
        }
    	/*if(entityName.trim().equals(" ")) {
            throw new DdmException("400", "User does not exists", HttpStatus.BAD_REQUEST);
    	}*/
    	User user = userRepository.findById(userId).get();
    	List<Entity> entities = entityRepository. findEntityByUserAndName(user, entityName.trim());
    	return userService.buildEntityResponse(entities);
    }
    
}
