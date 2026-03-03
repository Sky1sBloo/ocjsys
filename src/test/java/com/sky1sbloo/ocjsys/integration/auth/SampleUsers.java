package com.sky1sbloo.ocjsys.integration.auth;

import com.sky1sbloo.ocjsys.auth.AuthUser;
import com.sky1sbloo.ocjsys.auth.AuthUserRepository;
import com.sky1sbloo.ocjsys.auth.dto.LoginRequest;
import com.sky1sbloo.ocjsys.auth.dto.RegisterRequest;
import com.sky1sbloo.ocjsys.auth.role.Role;
import com.sky1sbloo.ocjsys.auth.role.RoleRepository;
import com.sky1sbloo.ocjsys.auth.role.Roles;
import com.sky1sbloo.ocjsys.userprofile.UserProfile;
import com.sky1sbloo.ocjsys.userprofile.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Used for initializing sample default and custom users
 */
@Service
@Profile("test")
public class SampleUsers {
    private final AuthUserRepository authUserRepository;
    private final UserProfileRepository userProfileRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private static RegisterRequest adminRegister;
    private static RegisterRequest userRegister;

    private final LoginRequest adminLogin;
    private final LoginRequest userLogin;

    @Autowired
    public SampleUsers(AuthUserRepository authUserRepository, UserProfileRepository userProfileRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.authUserRepository = authUserRepository;
        this.userProfileRepository = userProfileRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;

        adminRegister = RegisterRequest.builder()
                .username("admin")
                .password("password@1234")
                .name("Administrator").build();

        userRegister = RegisterRequest.builder()
                .username("user")
                .password("1234")
                .name("User").build();
        adminLogin = LoginRequest.builder()
                .username(adminRegister.getUsername())
                .password(adminRegister.getPassword()).build();
        userLogin = LoginRequest.builder()
                .username(userRegister.getUsername())
                .password(userRegister.getPassword())
                .build();
    }

    public LoginRequest getAdminLogin() {
        return adminLogin;
    }

    public LoginRequest getUserLogin() {
        return userLogin;
    }

    /**
     * Creates default user and admin
     */
    public void createUserAdmin() {
        Role adminRole = roleRepository.findByName(Roles.ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(null, Roles.ADMIN, new HashSet<>())));
        Role userRole = roleRepository.findByName(Roles.USER)
                .orElseGet(() -> roleRepository.save(new Role(null, Roles.USER, new HashSet<>())));
        createUser(adminRegister, Set.of(adminRole));
        createUser(userRegister, Set.of(userRole));
    }

    public void createUser(RegisterRequest registerRequest, Set<Role> roles) {
        if (authUserRepository.existsByUsername(registerRequest.getUsername())) {
            return;
        }

        AuthUser newUser = AuthUser.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .roles(roles)
                .build();
        AuthUser user = authUserRepository.save(newUser);
        UserProfile adminProfile = UserProfile.builder()
                .name(registerRequest.getName())
                .authUser(user).build();
        userProfileRepository.save(adminProfile);
        user.setUserProfile(adminProfile);
        authUserRepository.save(user);
    }
}