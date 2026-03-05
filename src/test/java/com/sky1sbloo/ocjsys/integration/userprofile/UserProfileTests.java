package com.sky1sbloo.ocjsys.integration.userprofile;

import com.sky1sbloo.ocjsys.integration.auth.SampleUsers;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sky1sbloo.ocjsys.auth.dto.LoginRequest;
import com.sky1sbloo.ocjsys.auth.dto.LoginResponse;
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
    private final ObjectMapper objectMapper;

    @Autowired
    public UserProfileTests(
            SampleUsers sampleUsers,
            RefreshTokenRepository refreshTokenRepository,
            MockMvc mockMvc,
            ObjectMapper objectMapper) {
        this.sampleUsers = sampleUsers;
        this.refreshTokenRepository = refreshTokenRepository;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
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
        String authorizationHeader = loginAndGetToken(sampleUsers.getAdminLogin());
        mockMvc.perform(get("/api/users/profile").header("Authorization", authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(sampleUsers.getAdminLogin().getUsername()));
    }

    @Test
    void userShouldReadProfile() throws Exception {
        String authorizationHeader = loginAndGetToken(sampleUsers.getUserLogin());
        mockMvc.perform(get("/api/users/profile").header("Authorization", authorizationHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(sampleUsers.getUserLogin().getUsername()));
    }

    @Test
    void adminShouldReadAllUserInfo() throws Exception {
        String authorizationHeader = loginAndGetToken(sampleUsers.getAdminLogin());
        mockMvc.perform(get("/api/users/").header("Authorization", authorizationHeader))
                .andExpect(status().isOk());
    }

    @Test
    void nonAdminShouldNotReadUserInfo() throws Exception {
        String authorizationHeader = loginAndGetToken(sampleUsers.getUserLogin());
        mockMvc.perform(get("/api/users/").header("Authorization", authorizationHeader))
                .andExpect(status().isForbidden());
    }

    private String loginAndGetToken(LoginRequest request) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andReturn();
        LoginResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), LoginResponse.class);
        return "Bearer " + response.getJwtToken();
    }
}
