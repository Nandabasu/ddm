package com.ddm.authorizationserver.payload;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.ddm.authorizationserver.model.Group;
import com.ddm.authorizationserver.model.Permission;
import com.ddm.authorizationserver.model.Role;

public class Profile implements Serializable{

	
	private static final long serialVersionUID = 1L;
	private String username;
    private String email;    
    private String fullName;
    private String occupation;
    private String pan;
    private LocalDate dob;
    private String mobile;
    private List<Role> roles;
    private List<Permission> authorities;
    private Group group;

    public Profile() { }
    
    public Profile(String username, String email, String fullName, String occupation, String pan,
			LocalDate dob, String mobile, List<Role> roles, List<Permission> authorities, Group group) {
		super();
		this.username = username;
		this.email = email;
		this.fullName = fullName;
		this.occupation = occupation;
		this.pan = pan;
		this.dob = dob;
		this.mobile = mobile;
		this.roles = roles;
		this.authorities = authorities;
		this.group = group;
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
	public String getPan() {
		return pan;
	}
	public void setPan(String pan) {
		this.pan = pan;
	}
	public LocalDate getDob() {
		return dob;
	}
	public void setDob(LocalDate dob) {
		this.dob = dob;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public List<Role> getRoles() {
		return roles;
	}
	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}
	public List<Permission> getAuthorities() {
		return authorities;
	}
	public void setAuthorities(List<Permission> authorities) {
		this.authorities = authorities;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}
	
}
