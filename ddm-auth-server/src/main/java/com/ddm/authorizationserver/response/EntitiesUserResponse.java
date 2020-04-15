package com.ddm.authorizationserver.response;

public class EntitiesUserResponse {

	private String userName;
	private String email;
	private String fullName;
	private String occupation;
	
	public EntitiesUserResponse() { }
	
	public EntitiesUserResponse(String userName, String email, String fullName, String occupation) {
		super();
		this.userName = userName;
		this.email = email;
		this.fullName = fullName;
		this.occupation = occupation;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getOccupation() {
		return occupation;
	}
	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}
	@Override
	public String toString() {
		return "EntitiesUserResponse [userName=" + userName + ", email=" + email + ", fullName=" + fullName
				+ ", occupation=" + occupation + "]";
	}
	
}
