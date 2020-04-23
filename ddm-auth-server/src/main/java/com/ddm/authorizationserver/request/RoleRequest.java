package com.ddm.authorizationserver.request;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.ddm.authorizationserver.model.Permission;

public class RoleRequest {

	@NotNull(message = "Rolen name cannot be null")
	private String roleName;

	private List<Permission> permissions;

	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public List<Permission> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}
	
	
}
