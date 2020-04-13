package com.ddm.authorizationserver.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddm.authorizationserver.model.Group;

@Repository
@Transactional
public interface GroupRepository extends JpaRepository<Group, Long>{

	boolean existsByName(String groupName);
}
