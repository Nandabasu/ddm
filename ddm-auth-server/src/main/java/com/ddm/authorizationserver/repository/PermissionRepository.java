package com.ddm.authorizationserver.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ddm.authorizationserver.model.Permission;

@Repository
@Transactional
public interface PermissionRepository extends JpaRepository<Permission, Long>{

}
