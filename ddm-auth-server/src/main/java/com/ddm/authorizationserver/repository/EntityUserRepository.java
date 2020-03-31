package com.ddm.authorizationserver.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddm.authorizationserver.model.EntityUser;

@Repository
@Transactional
public interface EntityUserRepository extends JpaRepository<EntityUser, Long> {

	Optional<EntityUser> findByEntityUserId(long userId);
	
	void deleteByEntityUserId(long id);
}
