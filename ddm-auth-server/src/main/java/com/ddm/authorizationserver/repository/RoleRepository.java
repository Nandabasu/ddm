package com.ddm.authorizationserver.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ddm.authorizationserver.model.Role;

@Repository
@Transactional
public interface RoleRepository extends JpaRepository<Role, Long>{

	Optional<Role>  findByName(String name);
	
	@Query("select r from Role r where r.name in (:roles)")
	Set<Role> findByName(@Param("roles") List<String> roles);

	List<Role> findAll();
	
	Optional<Role> findById(String id);
	
	Boolean existsByName(String roleName);
}
