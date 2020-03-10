package com.ddm.authorizationserver.util;

public class UserBasicInfo {

	private String username;
	private String fullName;
	private String email;
	private String mobile;
	
	private String groupName;
	
	public UserBasicInfo(String groupName) {
		super();
		this.groupName = groupName;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public UserBasicInfo(String username, String fullName, String email, String mobile) {
		super();
		this.username = username;
		this.fullName = fullName;
		this.email = email;
		this.mobile = mobile;
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
	@Override
	public String toString() {
		return "UserBasicInfo [username=" + username + ", email=" + email + ", mobile=" + mobile + "]";
	}
	
}
