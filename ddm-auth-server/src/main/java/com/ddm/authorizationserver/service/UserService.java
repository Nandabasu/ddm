package com.ddm.authorizationserver.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.ddm.authorizationserver.exception.DdmException;
import com.ddm.authorizationserver.model.*;
import com.ddm.authorizationserver.repository.*;
import com.ddm.authorizationserver.request.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ddm.authorizationserver.exception.RecordNotFoundException;
import com.ddm.authorizationserver.response.EntitiesUserResponse;
import com.ddm.authorizationserver.response.EntityResponse;
import com.ddm.authorizationserver.response.GroupResponse;
import com.ddm.authorizationserver.response.UserResponse;

@Service
public class UserService {
    @Autowired
    private UserDetailRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PermissionService permissionService;

    public UserResponse buildUserReponse(User userEntity) {
        UserResponse userResponse = buildFinalUserResponse(userEntity);
        return userResponse;
    }

    public List<EntityResponse> buildEntityResponse(List<Entities> entities) {
        List<EntityResponse> entityResponse = new ArrayList<EntityResponse>();
        for (Entities entity : entities) {
            EntitiesUserResponse user = buildFinalEntityResponse(entity.getUser());
            entityResponse.add(new EntityResponse(entity.getId(), entity.getName(), entity.getType(), user));
        }
        return entityResponse;
    }

    public EntityResponse buildEntityResponse(Entities entity) {
        EntitiesUserResponse user = buildFinalEntityResponse(entity.getUser());
        return new EntityResponse(entity.getId(), entity.getName(), entity.getType(), user);
    }

    public EntitiesUserResponse buildFinalEntityResponse(User user) {
        EntitiesUserResponse eUserResponse = new EntitiesUserResponse(user.getUsername(), user.getEmail(),
                user.getFullName(), user.getOccupation());
        return eUserResponse;
    }

    public List<UserResponse> buildUserReponse(List<User> userEntityList) {
        List<UserResponse> userResponseList = new ArrayList<>();
        for (User userEntity : userEntityList) {
            UserResponse userResponse = buildFinalUserResponse(userEntity);
            userResponseList.add(userResponse);
        }
        return userResponseList;
    }

    private UserResponse buildFinalUserResponse(User userEntity) {
        List<String> roles = userEntity.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        List<String> permissions = userEntity.getPermissions().stream().map(Permission::getName).collect(Collectors.toList());
        GroupResponse groupResponse = null;
        if (userEntity.getGroup() != null)
            groupResponse = new GroupResponse.GroupResponseBuilder(userEntity.getGroup().getId(),
                    userEntity.getGroup().getName(), userEntity.getGroup().getDescription()).build();
        UserResponse userResponse = new UserResponse.UserResponseBuilder(userEntity.getId(), userEntity.getUsername(),
                userEntity.getEmail(), userEntity.getFullName(), userEntity.getOccupation(), userEntity.getPan(),
                userEntity.getDob(), userEntity.getMobile(), roles, permissions).setGroup(groupResponse).build();
        return userResponse;
    }

    public List<UserResponse> getAllGroupAdmins() {
        List<User> userEntities = userRepository.getAllGroupAdmins();
        return buildUserReponse(userEntities);
    }

    public UserResponse getUserById(long id) throws RecordNotFoundException, DdmException {
        Optional<User> userEntity = userRepository.findById(id);
        if (userEntity.isPresent()) {
            User currentUser = getCurrentLoggedInUser();
            if (isValidUserToAccess(id, userEntity, currentUser)) {
                return buildUserReponse(userEntity.get());
            } else {
                throw new DdmException("401", "Access denied", HttpStatus.FORBIDDEN);
            }
        } else {
            throw new RecordNotFoundException("user does not exist!");
        }
    }

    private boolean isValidUserToAccess(long id, Optional<User> userEntity, User currentUser) {
        return (currentUser.getId() == id)
                || currentUser.getRoles().stream().anyMatch(role -> "MASTER_ADMIN".equalsIgnoreCase(role.getName()))
                || ((currentUser.getGroup().getId() == userEntity.get().getGroup().getId()) && currentUser.getRoles().stream().anyMatch(role -> "GROUP_ADMIN".equalsIgnoreCase(role.getName())))
                || (userEntity.get().getRoles().stream().anyMatch(role -> "USER".equalsIgnoreCase(role.getName())) && (currentUser.getGroup().getId() == userEntity.get().getGroup().getId()) &&
                currentUser.getRoles().stream().anyMatch(role -> ("USER".equalsIgnoreCase(role.getName()) && currentUser.getPermissions().stream().anyMatch(permission -> ("OTHER_USER_ACCESS".equalsIgnoreCase(permission.getName()))
                ))));
    }

    public User getCurrentLoggedInUser() {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository.findByUsername(principal.getUsername()).get();
        return currentUser;
    }

    public List<UserResponse> getAllUsersByGroupId(long groupId) {
        List<User> userEntities = userRepository.getAllUsersByGroupId(groupId);
        return buildUserReponse(userEntities);
    }

    public List<UserResponse> getAllUsersByGroupAdminId(long groupAdminId) throws RecordNotFoundException, DdmException {
        Optional<User> userEntity = userRepository.findById(groupAdminId);
        if (userEntity.isPresent()) {
            User currentUser = getCurrentLoggedInUser();
            if (isValidUserToAccess(groupAdminId, userEntity, currentUser)) {
                long groupId = userEntity.get().getGroup().getId();
                return getAllUsersByGroupId(groupId);
            } else {
                throw new DdmException("401", "Access denied", HttpStatus.FORBIDDEN);
            }

        } else {
            throw new RecordNotFoundException("Group admin does not exist!");
        }
    }

    public User createUserBasedOnRole(UserRequest profile) throws DdmException {
        if (userRepository.existsByEmail(profile.getEmail()) || userRepository.existsByUsername(profile.getUserName())) {
            throw new DdmException("400", "User already exists", HttpStatus.BAD_REQUEST);
        }
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Set<Role> roles = roleRepository.findByName(profile.getRoles());
        User currentUser = userRepository.findByUsername(principal.getUsername()).get();
        User result = null;
        if (roles.size() != 0) {
            if (principal.getRoles().stream().anyMatch(role -> "MASTER_ADMIN".equalsIgnoreCase(role.getName()))) {
                Group groupResult = getGroup(profile);
                if (groupResult != null) {
                    result = saveUser(profile, roles, groupResult);
                } else {
                    throw new DdmException("400", "Group already exists or invalid group id in the request", HttpStatus.BAD_REQUEST);
                }
            }
            if (principal.getRoles().stream().anyMatch(role -> ("GROUP_ADMIN".equalsIgnoreCase(role.getName()) || "USER".equalsIgnoreCase(role.getName())))) {
                if (roles.stream().anyMatch(role -> role.getName().equalsIgnoreCase("USER"))) {
                    result = saveUser(profile, roles, currentUser.getGroup());
                } else {
                    throw new DdmException("401", "Access denied : Not allowed to create " + profile.getRoles(), HttpStatus.FORBIDDEN);
                }
            }
            return result;
        } else {
            throw new DdmException("404", "Requested roles not found", HttpStatus.NOT_FOUND);
        }
    }

    private Group getGroup(UserRequest profile) {
        Group groupResult = null;
        if (profile.getGroupId() == null) {
            if (!groupRepository.existsByName(profile.getUserName())) {
                Group group = new Group(profile.getUserName(), profile.getFullName().concat(profile.getUserName()));
                group.setEnterprise(true);
                groupResult = groupRepository.save(group);
            }
        } else {
            Optional<Group> group = groupRepository.findById(profile.getGroupId());
            if (group.isPresent()) {
                groupResult = group.get();
            }
        }
        return groupResult;
    }

    private User saveUser(UserRequest profile, Set<Role> roles, Group group) {
        User user = new User(profile.getUserName(),
                passwordEncoder.encode(profile.getPassword()),
                profile.getEmail(),
                profile.getFullName(),
                profile.getOccupation(),
                profile.getPan(),
                profile.getDob(),
                profile.getMobile(),
                true,
                true, true, true, true,
                roles,
                group);
        user.setPermissions(permissionService.getPermissions(profile.getPrivileges(), roles));
        return userRepository.save(user);
    }
}
