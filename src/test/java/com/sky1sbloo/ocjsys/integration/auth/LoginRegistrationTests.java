package com.sky1sbloo.ocjsys.integration.auth;

import com.sky1sbloo.ocjsys.auth.AuthUser;
import com.sky1sbloo.ocjsys.auth.UserInfoRepository;
import com.sky1sbloo.ocjsys.auth.dto.LoginRequest;
import com.sky1sbloo.ocjsys.auth.dto.LoginResponse;
import com.sky1sbloo.ocjsys.auth.dto.RegisterRequest;
import com.sky1sbloo.ocjsys.auth.role.Role;
import com.sky1sbloo.ocjsys.auth.role.RoleRepository;
import com.sky1sbloo.ocjsys.auth.role.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LoginRegistrationTests {
    private final UserInfoRepository userInfoRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private final LoginRequest adminLogin;
    private final LoginRequest userLogin;

    @Autowired
    public LoginRegistrationTests(UserInfoRepository userInfoRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.userInfoRepository = userInfoRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
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

    @Test
    @Transactional
    void registerReturnsSuccess() throws Exception {
        String username = "test_user";
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username(username)
                .password("password1234").build();
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(username));
    }

    @Test
    @Transactional
    void loginSuccessReturnsTokens() throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(userLogin.getUsername()))
                .andReturn();

        LoginResponse loginResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), LoginResponse.class);
        String jwtToken = loginResponse.getJwtToken();
        mockMvc.perform(get("/profile")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(userLogin.getUsername()));
    }

    @Test
    void loginWrongCredentialsShouldFail() throws Exception {
        LoginRequest req = LoginRequest.builder()
                .username("user")
                .password("1234124")
                .build();
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void notEnoughAuthorityShouldFail() throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(userLogin.getUsername()))
                .andReturn();

        LoginResponse loginResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), LoginResponse.class);
        String jwtToken = loginResponse.getJwtToken();
        mockMvc.perform(get("/")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void adminShouldUpdateUserRole() throws Exception {
        LoginResponse response = loginAndGetResponse(adminLogin);
        LoginResponse userResponse = loginAndGetResponse(userLogin);
        URI updateRole = UriComponentsBuilder.fromUriString("/auth/role")
                .queryParam("id", userResponse.getId())
                .queryParam("roles", "ADMIN").build().toUri();
        String authorizationHeader = "Bearer " + response.getJwtToken();
        mockMvc.perform(put(updateRole).header("Authorization", authorizationHeader))
                .andExpect(status().isNoContent());
        mockMvc.perform(post("/auth/logout").header("Authorization", authorizationHeader));

        AuthUser user = userInfoRepository.findByUsername(userLogin.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(""));
        assertThat(user.getRoles().stream().map(Role::getName).toList())
                .contains(Roles.ADMIN);

        mockMvc.perform(get("/").header("Authorization", "Bearer " + userResponse.getJwtToken()))
                .andExpect(status().isOk());
    }

    private LoginResponse loginAndGetResponse(LoginRequest request) throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), LoginResponse.class);
    }

    private void createAdminUser(Role adminRole) {
        if (userInfoRepository.existsByUsername(userLogin.getUsername())) {
            return;
        }

        AuthUser adminUser = AuthUser.builder()
                .username(adminLogin.getUsername())
                .password(passwordEncoder.encode(adminLogin.getPassword()))
                .roles(Set.of(adminRole))
                .build();
        userInfoRepository.save(adminUser);
    }

    private void createNormalUser(Role userRole) {
        if (userInfoRepository.existsByUsername(userLogin.getUsername())) {
            return;
        }

        AuthUser testUser = AuthUser.builder()
                .username(userLogin.getUsername())
                .password(passwordEncoder.encode(userLogin.getPassword()))
                .roles(Set.of(userRole))
                .build();
        userInfoRepository.save(testUser);
    }
}
