package com.ddm.authorizationserver.util;

import java.time.LocalDate;

public class UserBasicInfo {

	private String username;
	private String fullName;
	private String email;
	private String mobile;
	private String groupName;
	private String pan;
	private String occupation;
	private LocalDate dob;

	public UserBasicInfo() {}
	
	public UserBasicInfo(String username, String fullName, String email, String mobile, String groupName, String pan,
			String occupation, LocalDate dob) {
		super();
		this.username = username;
		this.fullName = fullName;
		this.email = email;
		this.mobile = mobile;
		this.groupName = groupName;
		this.pan = pan;
		this.occupation = occupation;
		this.dob = dob;
	}


	public String getOccupation() {
		return occupation;
	}
	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}
	public LocalDate getDob() {
		return dob;
	}
	public void setDob(LocalDate dob) {
		this.dob = dob;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public String getPan() {
		return pan;
	}
	public void setPan(String pan) {
		this.pan = pan;
	}
	@Override
	public String toString() {
		return "UserBasicInfo [username=" + username + ", email=" + email + ", mobile=" + mobile + "]";
	}
	
}
