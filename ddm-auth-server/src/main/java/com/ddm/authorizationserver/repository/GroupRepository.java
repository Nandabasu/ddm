package com.ddm.authorizationserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddm.authorizationserver.model.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long>{

	/*@Query("select g from Group g where group_id = (select group_id from user_group where user_id in ( select id from user where username = ?1))")
	Optional<Group> findById(@Param("username") String username);*/
}
