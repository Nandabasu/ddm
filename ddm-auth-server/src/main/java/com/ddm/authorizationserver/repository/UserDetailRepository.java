package com.ddm.authorizationserver.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ddm.authorizationserver.model.User;

public interface UserDetailRepository extends JpaRepository<User,Integer> {


    Optional<User> findByUsername(String name);

	Optional<User> findByEmail(String email);
	
    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    
}
