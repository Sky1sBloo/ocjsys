package com.sky1sbloo.ocjsys.data;

import com.sky1sbloo.ocjsys.auth.UserInfoRepository;
import com.sky1sbloo.ocjsys.auth.role.PermissionRepository;
import com.sky1sbloo.ocjsys.auth.role.RoleRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

@Configuration
@Profile("dev")
public class DevDataConfig {
    @Bean
    @Order(1)
    public DataInitializer rolePermissionDataInitializer(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository
    ) {
        return new RolePermissionDataInitializer(roleRepository, permissionRepository);
    }

    @Bean
    @Order(2)
    public DataInitializer devDataInitializer(
            UserInfoRepository userInfoRepository,
            RoleRepository roleRepository
    ) {
        return new DevDataInitializer(userInfoRepository, roleRepository);
    }
}
