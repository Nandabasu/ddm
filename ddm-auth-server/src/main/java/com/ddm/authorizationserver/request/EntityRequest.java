package com.ddm.authorizationserver.request;

import javax.validation.constraints.NotNull;

public class EntityRequest {

	@NotNull(message = "Entity name cannot be null")
	private String entityName;
	
	@NotNull(message = "Entity type cannot be null")
	private String entityType;
	
	private long userId;
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public String getEntityType() {
		return entityType;
	}
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
	@Override
	public String toString() {
		return "EntityPayload [entityName=" + entityName + ", entityType=" + entityType + ", userId=" + userId + "]";
	}
	
}
