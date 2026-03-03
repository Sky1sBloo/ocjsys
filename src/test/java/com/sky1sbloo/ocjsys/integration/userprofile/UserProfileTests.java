package com.sky1sbloo.ocjsys.integration.userprofile;

import java.util.HashSet;
import java.util.Set;

import com.sky1sbloo.ocjsys.userprofile.UserProfile;
import com.sky1sbloo.ocjsys.userprofile.UserProfileRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sky1sbloo.ocjsys.auth.AuthUser;
import com.sky1sbloo.ocjsys.auth.UserInfoRepository;
import com.sky1sbloo.ocjsys.auth.dto.LoginRequest;
import com.sky1sbloo.ocjsys.auth.dto.LoginResponse;
import com.sky1sbloo.ocjsys.auth.refreshtoken.RefreshTokenRepository;
import com.sky1sbloo.ocjsys.auth.role.Role;
import com.sky1sbloo.ocjsys.auth.role.RoleRepository;
import com.sky1sbloo.ocjsys.auth.role.Roles;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserProfileTests {
    private final UserInfoRepository userInfoRepository;
    private final UserProfileRepository userProfileRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private final LoginRequest adminLogin;
    private final LoginRequest userLogin;

    @Autowired
    public UserProfileTests(UserInfoRepository userInfoRepository, UserProfileRepository userProfileRepository,
                            RoleRepository roleRepository,
                            PasswordEncoder passwordEncoder, RefreshTokenRepository refreshTokenRepository,
                            MockMvc mockMvc,
                            ObjectMapper objectMapper) {
        this.userInfoRepository = userInfoRepository;
        this.userProfileRepository = userProfileRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;

        adminLogin = LoginRequest.builder()
                .username("admin")
                .password("password@1234").build();

        userLogin = LoginRequest.builder()
                .username("user")
                .password("1234").build();
    }

    @BeforeEach
    void setup() {
        Role adminRole = roleRepository.findByName(Roles.ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(null, Roles.ADMIN, new HashSet<>())));
        Role userRole = roleRepository.findByName(Roles.USER)
                .orElseGet(() -> roleRepository.save(new Role(null, Roles.USER, new HashSet<>())));
        createAdminUser(adminRole);
        createNormalUser(userRole);
    }

    @AfterEach
    void cleanTokens() {
        refreshTokenRepository.deleteAll();
    }

    @Test
    void adminShouldReadProfile() throws Exception {
        String authorizationHeader = loginAndGetToken(adminLogin);
        mockMvc.perform(get("/profile").header("Authorization", authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(adminLogin.getUsername()));
    }

    @Test
    void userShouldReadProfile() throws Exception {
        String authorizationHeader = loginAndGetToken(userLogin);
        mockMvc.perform(get("/profile").header("Authorization", authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(userLogin.getUsername()));
    }

    @Test
    void adminShouldReadAllUserInfo() throws Exception {
        String authorizationHeader = loginAndGetToken(adminLogin);
        mockMvc.perform(get("/").header("Authorization", authorizationHeader))
                .andExpect(status().isOk());
    }

    @Test
    void nonAdminShouldNotReadUserInfo() throws Exception {
        String authorizationHeader = loginAndGetToken(userLogin);
        mockMvc.perform(get("/").header("Authorization", authorizationHeader))
                .andExpect(status().isForbidden());
    }

    private void createAdminUser(Role adminRole) {
        if (userInfoRepository.existsByUsername(userLogin.getUsername())) {
            return;
        }

        AuthUser newAdminUser = AuthUser.builder()
                .username(adminLogin.getUsername())
                .password(passwordEncoder.encode(adminLogin.getPassword()))
                .roles(Set.of(adminRole))
                .build();
        AuthUser adminUser = userInfoRepository.save(newAdminUser);
        UserProfile adminProfile = UserProfile.builder()
                .name("Administrator")
                .authUser(adminUser).build();
        userProfileRepository.save(adminProfile);
    }

    private void createNormalUser(Role userRole) {
        if (userInfoRepository.existsByUsername(userLogin.getUsername())) {
            return;
        }

        AuthUser newNormalUser = AuthUser.builder()
                .username(userLogin.getUsername())
                .password(passwordEncoder.encode(userLogin.getPassword()))
                .roles(Set.of(userRole))
                .build();
        AuthUser normalUser= userInfoRepository.save(newNormalUser);
        UserProfile adminProfile = UserProfile.builder()
                .name("Administrator")
                .authUser(normalUser).build();
        userProfileRepository.save(adminProfile);
    }

    private String loginAndGetToken(LoginRequest request) throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andReturn();
        LoginResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), LoginResponse.class);
        return "Bearer " + response.getJwtToken();
    }
}
