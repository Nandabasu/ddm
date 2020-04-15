package com.ddm.authorizationserver.response;

import java.time.LocalDate;
import java.util.List;

public class UserResponse {

	private String userName;
	private String email;
	private String fullName;
	private String occupation;
	private String pan;
	private LocalDate dob;
	private String mobile;
	private List<String> roles;
	private List<String> permissions;
	private GroupResponse group;

	public static class UserResponseBuilder {
		private String userName;
		private String email;
		private String fullName;
		private String occupation;
		private String pan;
		private LocalDate dob;
		private String mobile;
		private List<String> roles;
		private List<String> permissions;
//		private String entityType;
		private GroupResponse group;

		public UserResponseBuilder(String userName, String email, String fullName, String occupation, String pan,
				LocalDate dob, String mobile, List<String> roles, List<String> permissions) {
			super();
			this.userName = userName;
			this.email = email;
			this.fullName = fullName;
			this.occupation = occupation;
			this.pan = pan;
			this.dob = dob;
			this.mobile = mobile;
			this.roles = roles;
			this.permissions = permissions;
//			this.entityType = entityType;
		}

		public UserResponseBuilder setGroup(GroupResponse group) {
            this.group = group;
            return this;
        }
		
		public UserResponse build() {
			return new UserResponse(this);
		}

	}

	public UserResponse(UserResponseBuilder builder) {

		this.userName = builder.userName;
		this.email = builder.email;
		this.fullName = builder.fullName;
		this.occupation = builder.occupation;
		this.pan = builder.pan;
		this.dob = builder.dob;
		this.mobile = builder.mobile;
		this.group = builder.group;
		this.roles = builder.roles;
		this.permissions = builder.permissions;
	//	this.entityType = builder.entityType;
	}

	public String getUserName() {
		return userName;
	}

	public String getEmail() {
		return email;
	}

	public String getFullName() {
		return fullName;
	}

	public String getOccupation() {
		return occupation;
	}

	public String getPan() {
		return pan;
	}

	public LocalDate getDob() {
		return dob;
	}

	public String getMobile() {
		return mobile;
	}

	public List<String> getRoles() {
		return roles;
	}

	public List<String> getPermissions() {
		return permissions;
	}
	public GroupResponse getGroup() {
		return group;
	}

	@Override
	public String toString() {
		return "UserResponse [userName=" + userName + ", email=" + email + ", fullName=" + fullName + ", occupation="
				+ occupation + ", pan=" + pan + ", dob=" + dob + ", mobile=" + mobile + ", roles=" + roles
				+ ", permissions=" + permissions + ", group=" + group + "]";
	}

}
