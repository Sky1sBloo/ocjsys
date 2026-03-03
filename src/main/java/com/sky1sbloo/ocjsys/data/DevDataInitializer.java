package com.sky1sbloo.ocjsys.data;

import com.sky1sbloo.ocjsys.auth.AuthUser;
import com.sky1sbloo.ocjsys.auth.UserInfoRepository;
import com.sky1sbloo.ocjsys.auth.role.Role;
import com.sky1sbloo.ocjsys.auth.role.RoleRepository;
import com.sky1sbloo.ocjsys.auth.role.Roles;
import com.sky1sbloo.ocjsys.userprofile.UserProfile;
import com.sky1sbloo.ocjsys.userprofile.UserProfileRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Profile("dev")
@Component()
@DependsOn("rolePermissionDataInitializer")
public class DevDataInitializer implements DataInitializer {
    private final UserInfoRepository userInfoRepository;
    private final UserProfileRepository userProfileRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.data.dev.admin.username}")
    private String devAdminUsername;
    @Value("${spring.data.dev.admin.password}")
    private String devAdminPassword;

    public DevDataInitializer(UserInfoRepository userInfoRepository, UserProfileRepository userProfileRepository,
                              RoleRepository roleRepository,
                              PasswordEncoder passwordEncoder) {
        this.userInfoRepository = userInfoRepository;
        this.userProfileRepository = userProfileRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @PostConstruct
    public void initialize() {
        Role role = roleRepository.findByName(Roles.ADMIN).orElseThrow(
                () -> new IllegalStateException("Role admin does not exist. Ensure bean is initialized in order"));
        AuthUser newAdminUser = AuthUser.builder()
                .username(devAdminUsername)
                .password(passwordEncoder.encode(devAdminPassword))
                .roles(Set.of(role))
                .build();
        AuthUser adminUser = userInfoRepository.save(newAdminUser);
        UserProfile adminProfile = UserProfile.builder()
                .name("Administrator")
                .authUser(adminUser).build();
        userProfileRepository.save(adminProfile);
        adminUser.setUserProfile(adminProfile);
        userInfoRepository.save(adminUser);
    }
}
