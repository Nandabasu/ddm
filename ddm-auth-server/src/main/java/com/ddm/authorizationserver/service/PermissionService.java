package com.ddm.authorizationserver.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ddm.authorizationserver.model.Permission;
import com.ddm.authorizationserver.model.Role;
import com.ddm.authorizationserver.repository.PermissionRepository;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    public List<Permission> getPermissions(List<String> permissions, Set<Role> roles) {
        List<Permission> permissionList = new ArrayList<>();
        if (permissions == null || permissions.isEmpty()) {
            //permissionList = roles.stream().flatMap(role -> role.getPermissions().stream()).collect(Collectors.toList());
        } else {
            permissionRepository.findByName(permissions).forEach(permissionList::add);
        }
        return permissionList;
    }
}
