package com.sky1sbloo.ocjsys.integration.userprofile;

import com.sky1sbloo.ocjsys.integration.Authenticator;
import com.sky1sbloo.ocjsys.integration.auth.SampleUsers;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sky1sbloo.ocjsys.auth.refreshtoken.RefreshTokenRepository;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserProfileTests {
    private final SampleUsers sampleUsers;
    private final RefreshTokenRepository refreshTokenRepository;

    private final MockMvc mockMvc;
    private final Authenticator authenticator;

    @Autowired
    public UserProfileTests(
            SampleUsers sampleUsers,
            RefreshTokenRepository refreshTokenRepository,
            MockMvc mockMvc,
            Authenticator authenticator) {
        this.sampleUsers = sampleUsers;
        this.refreshTokenRepository = refreshTokenRepository;
        this.mockMvc = mockMvc;
        this.authenticator = authenticator;
    }

    @BeforeEach
    void setup() {
        sampleUsers.createUserAdmin();
    }

    @AfterEach
    void cleanTokens() {
        refreshTokenRepository.deleteAll();
    }

    @Test
    void adminShouldReadProfile() throws Exception {
        String authorizationHeader = authenticator.loginAndGetToken(sampleUsers.getAdminLogin());
        mockMvc.perform(get("/api/users/profile").header("Authorization", authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(sampleUsers.getAdminLogin().getUsername()));
    }

    @Test
    void userShouldReadProfile() throws Exception {
        String authorizationHeader = authenticator.loginAndGetToken(sampleUsers.getUserLogin());
        mockMvc.perform(get("/api/users/profile").header("Authorization", authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(sampleUsers.getUserLogin().getUsername()));
    }

    @Test
    void adminShouldReadAllUserInfo() throws Exception {
        String authorizationHeader = authenticator.loginAndGetToken(sampleUsers.getAdminLogin());
        mockMvc.perform(get("/api/users/").header("Authorization", authorizationHeader))
                .andExpect(status().isOk());
    }

    @Test
    void nonAdminShouldNotReadUserInfo() throws Exception {
        String authorizationHeader = authenticator.loginAndGetToken(sampleUsers.getUserLogin());
        mockMvc.perform(get("/api/users/").header("Authorization", authorizationHeader))
                .andExpect(status().isForbidden());
    }
}
