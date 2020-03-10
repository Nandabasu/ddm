package com.ddm.authorizationserver.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ddm.authorizationserver.model.Group;
import com.ddm.authorizationserver.model.UserGroup;

@Repository
@Transactional
public interface UserGroupRepository extends JpaRepository<UserGroup, Long>{

	List<UserGroup> findByUsername(String username);
	
	List<UserGroup> findByGroup(Group group);

}
