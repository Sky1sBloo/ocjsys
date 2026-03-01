package com.sky1sbloo.ocjsys.integration.user;

import com.sky1sbloo.ocjsys.auth.UserInfo;
import com.sky1sbloo.ocjsys.auth.UserInfoRepository;
import com.sky1sbloo.ocjsys.auth.dto.LoginRequest;
import com.sky1sbloo.ocjsys.auth.dto.LoginResponse;
import com.sky1sbloo.ocjsys.auth.refreshtoken.RefreshTokenRepository;
import com.sky1sbloo.ocjsys.auth.role.Role;
import com.sky1sbloo.ocjsys.auth.role.RoleRepository;
import com.sky1sbloo.ocjsys.auth.role.Roles;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminTests {
    private final UserInfoRepository userInfoRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private final LoginRequest adminLogin;
    private final LoginRequest userLogin;

    @Autowired
    public AdminTests(UserInfoRepository userInfoRepository,
                      RoleRepository roleRepository,
                      PasswordEncoder passwordEncoder, RefreshTokenRepository refreshTokenRepository,
                      MockMvc mockMvc,
                      ObjectMapper objectMapper) {
        this.userInfoRepository = userInfoRepository;
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
        createAdminUser();
        createNormalUser();
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

    private void createAdminUser() {
        Role adminRole = roleRepository.findByName(Roles.ADMIN).orElseThrow();
        if (userInfoRepository.existsByUsername(userLogin.getUsername())) {
            return;
        }

        UserInfo adminUser = UserInfo.builder()
                .username(adminLogin.getUsername())
                .password(passwordEncoder.encode(adminLogin.getPassword()))
                .roles(Set.of(adminRole))
                .build();
        userInfoRepository.save(adminUser);
    }

    private void createNormalUser() {
        Role userRole = roleRepository.findByName(Roles.USER).orElseThrow();
        if (userInfoRepository.existsByUsername(userLogin.getUsername())) {
            return;
        }

        UserInfo testUser = UserInfo.builder()
                .username(userLogin.getUsername())
                .password(passwordEncoder.encode(userLogin.getPassword()))
                .roles(Set.of(userRole))
                .build();
        userInfoRepository.save(testUser);
    }

    private String loginAndGetToken(LoginRequest request) throws Exception {
        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andReturn();
        LoginResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), LoginResponse.class);
        return "Bearer " + response.getJwtToken();
    }
}
