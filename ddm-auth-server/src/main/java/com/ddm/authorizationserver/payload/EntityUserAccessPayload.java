package com.ddm.authorizationserver.payload;

public class EntityUserAccessPayload {

	private long entityUserId;
	private long userId;
	
	public long getEntityUserId() {
		return entityUserId;
	}
	public void setEntityUserId(long entityUserId) {
		this.entityUserId = entityUserId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	
}
