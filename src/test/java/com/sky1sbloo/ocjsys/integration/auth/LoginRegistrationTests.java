package com.sky1sbloo.ocjsys.integration.auth;

import com.sky1sbloo.ocjsys.auth.UserInfo;
import com.sky1sbloo.ocjsys.auth.UserInfoRepository;
import com.sky1sbloo.ocjsys.auth.dto.LoginRequest;
import com.sky1sbloo.ocjsys.auth.dto.LoginResponse;
import com.sky1sbloo.ocjsys.auth.dto.RegisterRequest;
import com.sky1sbloo.ocjsys.auth.role.Role;
import com.sky1sbloo.ocjsys.auth.role.RoleRepository;
import com.sky1sbloo.ocjsys.auth.role.Roles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LoginRegistrationTests {
    private final UserInfoRepository userInfoRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public LoginRegistrationTests(UserInfoRepository userInfoRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.userInfoRepository = userInfoRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    @Transactional
    void registerReturnsSuccess() throws Exception {
        String username = "test_user";
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username(username)
                .password("password1234").build();
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(username));
    }

    @Test
    @Transactional
    void loginSuccessReturnsTokens() throws Exception {
        registerUserRole();
        LoginRequest req = LoginRequest.builder()
                .username("test_user")
                .password("password1234").build();

        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test_user"))
                .andReturn();

        LoginResponse loginResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), LoginResponse.class);
        String jwtToken = loginResponse.getJwtToken();
        mockMvc.perform(get("/profile")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test_user"));
    }

    @Test
    void loginWrongCredentialsShouldFail() throws Exception {
        LoginRequest req = LoginRequest.builder()
                .username("user")
                .password("1234124")
                .build();
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void notEnoughAuthorityShouldFail() throws Exception {
        registerUserRole();

        LoginRequest req = LoginRequest.builder()
                .username("test_user")
                .password("password1234").build();

        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test_user"))
                .andReturn();

        LoginResponse loginResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), LoginResponse.class);
        String jwtToken = loginResponse.getJwtToken();
        mockMvc.perform(get("/")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isForbidden());
    }

    private void registerUserRole() {
        Role userRole = roleRepository.findByName(Roles.USER).orElseThrow();
        UserInfo testUser = UserInfo.builder()
        .username("test_user")
        .password(passwordEncoder.encode("password1234"))
        .roles(Set.of(userRole))
        .build();
        userInfoRepository.save(testUser);
    }
}
