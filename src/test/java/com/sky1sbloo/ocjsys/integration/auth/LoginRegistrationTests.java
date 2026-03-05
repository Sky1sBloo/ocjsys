package com.sky1sbloo.ocjsys.integration.auth;

import com.sky1sbloo.ocjsys.auth.AuthUser;
import com.sky1sbloo.ocjsys.auth.AuthUserRepository;
import com.sky1sbloo.ocjsys.auth.dto.LoginRequest;
import com.sky1sbloo.ocjsys.auth.dto.LoginResponse;
import com.sky1sbloo.ocjsys.auth.dto.RegisterRequest;
import com.sky1sbloo.ocjsys.auth.role.Role;
import com.sky1sbloo.ocjsys.auth.role.Roles;
import com.sky1sbloo.ocjsys.integration.Authenticator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class LoginRegistrationTests {
    private final AuthUserRepository authUserRepository;
    private final SampleUsers sampleUsers;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final Authenticator authenticator;

    @Autowired
    public LoginRegistrationTests(AuthUserRepository authUserRepository,
                                  SampleUsers sampleUsers,
                                  MockMvc mockMvc,
                                  ObjectMapper objectMapper,
                                  Authenticator authenticator) {
        this.authUserRepository = authUserRepository;
        this.sampleUsers = sampleUsers;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.authenticator = authenticator;
    }

    @BeforeEach
    void setup() {
        sampleUsers.createUserAdmin();
    }

    @Test
    void registerReturnsSuccess() throws Exception {
        String username = "test_user";
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username(username)
                .password("password1234").build();
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(username));
    }

    @Test
    void loginSuccessReturnsTokens() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleUsers.getUserLogin())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(sampleUsers.getUserLogin().getUsername()));
    }

    @Test
    void loginWrongCredentialsShouldFail() throws Exception {
        LoginRequest req = LoginRequest.builder()
                .username("user")
                .password("1234124")
                .build();
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void notEnoughAuthorityShouldFail() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleUsers.getUserLogin())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(sampleUsers.getUserLogin().getUsername()))
                .andReturn();

        LoginResponse loginResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), LoginResponse.class);
        String jwtToken = loginResponse.getJwtToken();
        mockMvc.perform(get("/api/users/")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminShouldUpdateUserRole() throws Exception {
        LoginResponse response = authenticator.loginAndGetResponse(sampleUsers.getAdminLogin());
        LoginResponse userResponse = authenticator.loginAndGetResponse(sampleUsers.getUserLogin());
        URI updateRole = UriComponentsBuilder.fromUriString("/api/auth/role")
                .queryParam("id", userResponse.getId())
                .queryParam("roles", "ADMIN").build().toUri();
        String authorizationHeader = "Bearer " + response.getJwtToken();
        mockMvc.perform(put(updateRole).header("Authorization", authorizationHeader))
                .andExpect(status().isNoContent());
        mockMvc.perform(post("/api/auth/logout").header("Authorization", authorizationHeader));

        AuthUser user = authUserRepository.findByUsername(sampleUsers.getUserLogin().getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(""));
        assertThat(user.getRoles().stream().map(Role::getName).toList())
                .contains(Roles.ADMIN);

        mockMvc.perform(get("/api/users/").header("Authorization", "Bearer " + userResponse.getJwtToken()))
                .andExpect(status().isOk());
    }
}
