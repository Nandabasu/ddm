package com.ddm.authorizationserver.controller;

import java.util.List;

import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ddm.authorizationserver.model.Group;
import com.ddm.authorizationserver.repository.GroupRepository;
import com.ddm.authorizationserver.request.GroupRequest;
import com.ddm.authorizationserver.response.ApiResponse;
import com.ddm.authorizationserver.response.GroupResponse;
import com.ddm.authorizationserver.response.UserResponse;
import com.ddm.authorizationserver.service.GroupService;
import com.ddm.authorizationserver.service.UserService;

@RequestMapping("v1/groups")
@RestController
public class GroupResource {

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	GroupService groupService;
	
	@Autowired
	UserService userService;

	@PostMapping
	@PreAuthorize("hasAuthority('MASTER_ADMIN')")
	public ResponseEntity<?> createGroup(@Valid @RequestBody GroupRequest group) {

		if (groupRepository.existsByName(group.getName())) {
			return new ResponseEntity<>(new ApiResponse(false, "Group name already exists"), HttpStatus.BAD_REQUEST);
		}

		Group groupObj = new Group(group.getName(), group.getDescription());
		Group groupEntity = groupRepository.save(groupObj);
		GroupResponse groupResponse = groupService.buildGroupReponse(groupEntity);
		return new ResponseEntity<GroupResponse>(groupResponse, new HttpHeaders(), HttpStatus.CREATED);
	}

	@GetMapping
	@ResponseBody
	@PreAuthorize("hasAuthority('MASTER_ADMIN')")
	public ResponseEntity<List<GroupResponse>> getAllGroups() {
		List<Group> groupEntityList = groupRepository.findAll();
		List<GroupResponse> groupResponseList = groupService.buildGroupReponse(groupEntityList);
		return new ResponseEntity<List<GroupResponse>>(groupResponseList, new HttpHeaders(), HttpStatus.OK);
	}

	@GetMapping(value = "/{id}")
	@PreAuthorize("hasAuthority('MASTER_ADMIN') or hasAuthority('GROUP_ADMIN')")
	public ResponseEntity<?> getGroupById(@PathVariable long id) {

		if (!groupRepository.existsById(id)) {
			return new ResponseEntity<>(new ApiResponse(false, "Group does not exists"), HttpStatus.BAD_REQUEST);
		}

		Group groupEntity = groupRepository.findById(id).get();
		GroupResponse groupResponse = groupService.buildGroupReponse(groupEntity);
		return new ResponseEntity<GroupResponse>(groupResponse, new HttpHeaders(), HttpStatus.OK);
	}

	@PutMapping
	@PreAuthorize("hasAuthority('MASTER_ADMIN')")
	public ResponseEntity<?> updateGroup(@Valid @RequestBody GroupRequest group) {

		if (!groupRepository.existsById(group.getId())) {
			return new ResponseEntity<>(new ApiResponse(false, "Group does not exists"), HttpStatus.BAD_REQUEST);
		}
		Group groupResult = groupRepository.findById(group.getId()).get();
		groupResult.setName(group.getName());
		groupResult.setDescription(group.getDescription());
		Group groupEntity = groupRepository.save(groupResult);
		GroupResponse groupResponse = groupService.buildGroupReponse(groupEntity);
		return new ResponseEntity<GroupResponse>(groupResponse, new HttpHeaders(), HttpStatus.OK);
	}

	@DeleteMapping(value = "/{id}")
	@PreAuthorize("hasAuthority('MASTER_ADMIN')")
	public ResponseEntity<?> deleteGroup(@PathVariable("id") long id) {

		if (!groupRepository.existsById(id)) {
			return new ResponseEntity<>(new ApiResponse(false, "Group does not exists"), HttpStatus.BAD_REQUEST);
		}

		groupRepository.deleteById(id);
		return ResponseEntity.ok().body(new ApiResponse(true, "Group Deleted Successfully"));
	}
	
	@GetMapping(value = "/{id}/users")
	@PreAuthorize("hasAuthority('MASTER_ADMIN') or hasAuthority('GROUP_ADMIN')")
	public ResponseEntity<?> getAllUsersByGroupId(@PathVariable long id) {

		if (!groupRepository.existsById(id)) {
			return new ResponseEntity<>(new ApiResponse(false, "Group does not exists"), HttpStatus.BAD_REQUEST);
		}
		
		List<UserResponse> userResponseList=userService.getAllUsersByGroupId(id);
		return new ResponseEntity<List<UserResponse>>(userResponseList, new HttpHeaders(), HttpStatus.OK);
	}

}
