package com.sky1sbloo.ocjsys.data;

import com.sky1sbloo.ocjsys.auth.UserInfo;
import com.sky1sbloo.ocjsys.auth.UserInfoRepository;
import com.sky1sbloo.ocjsys.auth.role.Role;
import com.sky1sbloo.ocjsys.auth.role.RoleRepository;
import com.sky1sbloo.ocjsys.auth.role.Roles;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Profile("dev")
@RequiredArgsConstructor
@Component()
@DependsOn("rolePermissionDataInitializer")
public class DevDataInitializer implements DataInitializer {
    private final UserInfoRepository userInfoRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.data.dev.admin.username}")
    private String devAdminUsername;
    @Value("${spring.data.dev.admin.password}")
    private String devAdminPassword;

    @Override
    @PostConstruct
    public void initialize() {
        Role role = roleRepository.findByName(Roles.ADMIN).orElseThrow(
                () -> new IllegalStateException("Role admin does not exist. Ensure bean is initialized in order"));
        UserInfo adminUser = UserInfo.builder()
                .username(devAdminUsername)
                .password(passwordEncoder.encode(devAdminPassword))
                .roles(Set.of(role))
                .build();
        userInfoRepository.save(adminUser);
    }
}
