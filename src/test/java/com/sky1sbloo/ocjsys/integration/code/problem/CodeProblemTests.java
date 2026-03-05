package com.sky1sbloo.ocjsys.integration.code.problem;

import com.sky1sbloo.ocjsys.auth.dto.LoginResponse;
import com.sky1sbloo.ocjsys.code.problem.dto.CodeProblemCreateDto;
import com.sky1sbloo.ocjsys.integration.Authenticator;
import com.sky1sbloo.ocjsys.integration.auth.SampleUsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CodeProblemTests {
    private final SampleUsers sampleUsers;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    private Authenticator authenticator;

    @Autowired
    public CodeProblemTests(SampleUsers sampleUsers, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.sampleUsers = sampleUsers;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }


    @BeforeEach
    void setup() {
        authenticator = new Authenticator(mockMvc, objectMapper);
        sampleUsers.createUserAdmin();
    }

    @Test
    void createCodeProblemShouldSucceed() throws Exception {
        String authToken = authenticator.loginAndGetToken(sampleUsers.getUserLogin());
        CodeProblemCreateDto createDto = new CodeProblemCreateDto(
                "Two Sum",
                List.of("array", "hash-table"),
                "EASY",
                "Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target.",
                "Use a hash map to store the indices of the numbers and check for the complement."
        );
        mockMvc.perform(post("/api/code/problems")
                        .header("Authorization", authToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void getCodeProblemShouldSucceed() throws Exception {
        LoginResponse loginResponse = authenticator.loginAndGetResponse(sampleUsers.getUserLogin());
        CodeProblemCreateDto createDto = new CodeProblemCreateDto(
                "Two Sum",
                List.of("array", "hash-table"),
                "EASY",
                "Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target.",
                "Use a hash map to store the indices of the numbers and check for the complement."
        );
        mockMvc.perform(post("/api/code/problems")
                        .header("Authorization", "Bearer " + loginResponse.getJwtToken())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/code/problems?owner=" + loginResponse.getName())
                        .header("Authorization", "Bearer " + loginResponse.getJwtToken()))
                .andDo(result -> System.out.println("Response: " + result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Two Sum"))
                .andExpect(jsonPath("$[0].difficulty").value("EASY"))
                .andExpect(jsonPath("$[0].owner.name").value(loginResponse.getName()));
    }
}
