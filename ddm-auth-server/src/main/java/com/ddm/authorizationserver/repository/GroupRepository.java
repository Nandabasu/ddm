package com.ddm.authorizationserver.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ddm.authorizationserver.model.Group;

@Repository
@Transactional
public interface GroupRepository extends JpaRepository<Group, Long>{

	boolean existsByName(String groupName);

	@Query(value = "update DDM_GROUP g set group_name=:groupName, description =:description where id=:groupId", nativeQuery = true)
	void updateGroup(@Param("groupName") String groupName, @Param("description") String description, @Param("groupId") long groupId );

}
