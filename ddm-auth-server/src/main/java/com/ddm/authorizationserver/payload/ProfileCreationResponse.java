package com.ddm.authorizationserver.payload;

public class ProfileCreationResponse extends ApiResponse{

	private Profile profile;

	public ProfileCreationResponse() { }
	
	public ProfileCreationResponse(Profile profile) {
		super();
		this.profile = profile;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	
}
