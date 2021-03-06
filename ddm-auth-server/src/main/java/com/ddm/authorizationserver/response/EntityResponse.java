package com.ddm.authorizationserver.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class EntityResponse {

	private long id;
	private String name;
	private String type;
	@JsonInclude(Include.NON_NULL)
	private EntitiesUserResponse user;
	
	public EntityResponse() { }
	
	public EntityResponse(long id, String name, String type, EntitiesUserResponse user) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.user = user;
	}

	public EntityResponse(long id, String name, String type) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public EntitiesUserResponse getUser() {
		return user;
	}

	public void setUser(EntitiesUserResponse user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "EntityResponse [id=" + id + ", name=" + name + ", type=" + type + ", user=" + user + "]";
	}

}
