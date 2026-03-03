package com.sky1sbloo.ocjsys.integration.auth;

import com.sky1sbloo.ocjsys.auth.AuthUser;
import com.sky1sbloo.ocjsys.auth.UserInfoRepository;
import com.sky1sbloo.ocjsys.auth.dto.LoginRequest;
import com.sky1sbloo.ocjsys.auth.dto.LoginResponse;
import com.sky1sbloo.ocjsys.auth.dto.RegisterRequest;
import com.sky1sbloo.ocjsys.auth.role.Role;
import com.sky1sbloo.ocjsys.auth.role.Roles;
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
    private final UserInfoRepository userInfoRepository;
    private final SampleUsers sampleUsers;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public LoginRegistrationTests(UserInfoRepository userInfoRepository,
                                  SampleUsers sampleUsers,
                                  MockMvc mockMvc,
                                  ObjectMapper objectMapper) {
        this.userInfoRepository = userInfoRepository;
        this.sampleUsers = sampleUsers;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
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
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(username));
    }

    @Test
    void loginSuccessReturnsTokens() throws Exception {
        mockMvc.perform(post("/auth/login")
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
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void notEnoughAuthorityShouldFail() throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleUsers.getUserLogin())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(sampleUsers.getUserLogin().getUsername()))
                .andReturn();

        LoginResponse loginResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), LoginResponse.class);
        String jwtToken = loginResponse.getJwtToken();
        mockMvc.perform(get("/")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminShouldUpdateUserRole() throws Exception {
        LoginResponse response = loginAndGetResponse(sampleUsers.getAdminLogin());
        LoginResponse userResponse = loginAndGetResponse(sampleUsers.getUserLogin());
        URI updateRole = UriComponentsBuilder.fromUriString("/auth/role")
                .queryParam("id", userResponse.getId())
                .queryParam("roles", "ADMIN").build().toUri();
        String authorizationHeader = "Bearer " + response.getJwtToken();
        mockMvc.perform(put(updateRole).header("Authorization", authorizationHeader))
                .andExpect(status().isNoContent());
        mockMvc.perform(post("/auth/logout").header("Authorization", authorizationHeader));

        AuthUser user = userInfoRepository.findByUsername(sampleUsers.getUserLogin().getUsername())
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
}
