package com.ddm.authorizationserver.payload;

import java.util.List;

import javax.validation.constraints.NotBlank;

public class ProfileCreation {

	private CommonUserInfo commonUserInfo;
	private List<String> roles;
	private String entityType;
	
	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) { 
		this.roles = roles;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public CommonUserInfo getCommonUserInfo() {
		return commonUserInfo;
	}

	public void setCommonUserInfo(CommonUserInfo commonUserInfo) {
		this.commonUserInfo = commonUserInfo;
	}
	
	

}
