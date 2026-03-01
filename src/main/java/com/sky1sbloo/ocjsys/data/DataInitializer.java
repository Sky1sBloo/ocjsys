package com.sky1sbloo.ocjsys.data;

import com.sky1sbloo.ocjsys.auth.role.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@RequiredArgsConstructor
@Component
public class DataInitializer {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @PostConstruct
    public void initRolesAndPermissions() {
        if (roleRepository.findByName(Roles.USER).isEmpty()) {
            Role user = new Role();
            user.setName(Roles.USER);
            roleRepository.save(user);
        }

        if (roleRepository.findByName(Roles.ADMIN).isEmpty()) {
            Permission readUsers = permissionRepository.save(new Permission(null, "READ_USERS_INFO"));
            Permission changeUserRole = permissionRepository.save(new Permission(null, "CHANGE_USER_ROLE"));
            Role admin = new Role();
            admin.setName(Roles.ADMIN);
            admin.setPermissions(Set.of(readUsers, changeUserRole));
            roleRepository.save(admin);
        }
    }
}
