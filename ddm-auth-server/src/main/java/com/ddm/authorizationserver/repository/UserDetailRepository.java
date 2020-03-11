package com.ddm.authorizationserver.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddm.authorizationserver.model.User;

@Repository
@Transactional
public interface UserDetailRepository extends JpaRepository<User,Integer> {

	
    Optional<User> findByUsername(String name);

	Optional<User> findByEmail(String email);
	
    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    
}
