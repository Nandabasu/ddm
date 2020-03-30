package com.ddm.authorizationserver.response;

public class GroupResponse{
	
	private long id;
	private String name;
	private String description;
	
	public static class GroupResponseBuilder {
		private long id;
		private String name;
		private String description;
		public GroupResponseBuilder(long id, String name, String description) {
			super();
			this.id = id;
			this.name = name;
			this.description = description;
		}
		
		public GroupResponse build() {
			return new GroupResponse(this);
		}
	}
	
	
	
	public GroupResponse(GroupResponseBuilder builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.description = builder.description;
	}



	public long getId() {
		return id;
	}



	public String getName() {
		return name;
	}



	public String getDescription() {
		return description;
	}



	@Override
	public String toString() {
		return "GroupResponse [id=" + id + ", name=" + name + ", description=" + description + "]";
	}
	
}
	



	