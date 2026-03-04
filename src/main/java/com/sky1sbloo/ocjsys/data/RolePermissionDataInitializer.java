package com.sky1sbloo.ocjsys.data;

import com.sky1sbloo.ocjsys.auth.role.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@RequiredArgsConstructor
@Component("rolePermissionDataInitializer")
public class RolePermissionDataInitializer implements DataInitializer{
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @PostConstruct
    @Override
    public void initialize() {
        Set<Permission> userPermissions = Set.of(
                getOrCreatePermission(Permissions.CREATE_CODE_PROBLEMS)
        );
        createRoleIfNotExists(Roles.USER, Set.of());
        Set<Permission> adminPermissions = Set.of(
                getOrCreatePermission(Permissions.READ_USERS_INFO),
                getOrCreatePermission(Permissions.CHANGE_USER_ROLE)
        );
        createRoleIfNotExists(Roles.ADMIN, adminPermissions);
    }

    private Permission getOrCreatePermission(Permissions permissionName) {
        return permissionRepository.findByName(permissionName).orElseGet(
                () -> permissionRepository.save(
                        new Permission(null, permissionName))
        );
    }

    private void createRoleIfNotExists(Roles roleName, Set<Permission> permissions) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = new Role();
            role.setName(roleName);
            role.setPermissions(permissions);
            roleRepository.save(role);
        }
    }
}
