package com.ddm.authorizationserver.repository;

import javax.transaction.Transactional;

import com.ddm.authorizationserver.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ddm.authorizationserver.model.Permission;

import java.util.List;
import java.util.Set;

@Repository
@Transactional
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    @Query("select permission from Permission permission where permission.name in (:permissions)")
    Set<Permission> findByName(@Param("permissions") List<String> permissions);
}
