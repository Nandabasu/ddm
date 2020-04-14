package com.ddm.authorizationserver.payload;

public class ModifyUserPayload {

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
