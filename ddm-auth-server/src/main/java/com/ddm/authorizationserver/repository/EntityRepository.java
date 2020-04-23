package com.ddm.authorizationserver.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ddm.authorizationserver.model.Entity;
import com.ddm.authorizationserver.model.User;

@Repository
@Transactional
public interface EntityRepository extends JpaRepository<Entity, Long>{

	Boolean existsByName(String entityName);
		
	Boolean existsById(long id);
	
	List<Entity> findByUserId(long userId);
	
	@Query("select e from Entity e where e.user =:user and e.name like %:entityName%")
	List<Entity> findEntityByUserAndName(@Param("user") User user, @Param("entityName") String entityName);
}

