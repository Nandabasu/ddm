package com.ddm.authorizationserver.request;

public class UpdateUserRequest {

	private long id;
	private String occupation;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getOccupation() {
		return occupation;
	}
	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}
	
}
