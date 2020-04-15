package com.ddm.authorizationserver.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddm.authorizationserver.model.Entities;

@Repository
@Transactional
public interface EntitiesRepository extends JpaRepository<Entities, Long>{

	Boolean existsByName(String entityName);
		
	Boolean existsById(long id);
}

