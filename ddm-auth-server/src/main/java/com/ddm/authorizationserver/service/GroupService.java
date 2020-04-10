package com.ddm.authorizationserver.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ddm.authorizationserver.model.Group;
import com.ddm.authorizationserver.response.GroupResponse;

@Service
public class GroupService {

	public GroupResponse buildGroupReponse(Group groupEntity) {
		GroupResponse groupResponse = buildFinalGroupResponse(groupEntity);
		return groupResponse;
	}

	public List<GroupResponse> buildGroupReponse(List<Group> groupEntityList) {
		List<GroupResponse> groupResponseList = new ArrayList<>();
		for (Group groupEntity : groupEntityList) {
			GroupResponse groupResponse = buildFinalGroupResponse(groupEntity);
			groupResponseList.add(groupResponse);
		}
		return groupResponseList;
	}

	private GroupResponse buildFinalGroupResponse(Group groupEntity) {
		GroupResponse groupResponse = new GroupResponse.GroupResponseBuilder(groupEntity.getId(), groupEntity.getName(),
				groupEntity.getDescription()).build();
		return groupResponse;
	}
}
