package com.ddm.authorizationserver.service;

import java.sql.SQLException;

import com.ddm.authorizationserver.payload.ProfileCreationResponse;
import com.ddm.authorizationserver.payload.UserRegistrationRequestPayload;

public interface ProfileService {

	public ProfileCreationResponse createUser(UserRegistrationRequestPayload payload) throws SQLException;
}
