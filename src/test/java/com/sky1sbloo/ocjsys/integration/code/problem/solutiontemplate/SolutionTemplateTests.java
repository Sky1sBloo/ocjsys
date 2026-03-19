package com.sky1sbloo.ocjsys.integration.code.problem.solutiontemplate;

import com.sky1sbloo.ocjsys.code.CodeLanguage;
import com.sky1sbloo.ocjsys.code.problem.Difficulties;
import com.sky1sbloo.ocjsys.code.problem.dto.CodeProblemCreateDto;
import com.sky1sbloo.ocjsys.code.problem.dto.CodeProblemResponseDto;
import com.sky1sbloo.ocjsys.code.problem.solutiontemplate.dto.SolutionTemplateDto;
import com.sky1sbloo.ocjsys.integration.Authenticator;
import com.sky1sbloo.ocjsys.integration.auth.SampleUsers;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class SolutionTemplateTests {
    private final SampleUsers sampleUsers;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final Authenticator authenticator;

    @Autowired
    public SolutionTemplateTests(SampleUsers sampleUsers, MockMvc mockMvc, ObjectMapper objectMapper, Authenticator authenticator) {
        this.sampleUsers = sampleUsers;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.authenticator = authenticator;
    }

    @BeforeEach
    public void setup() throws Exception {
        sampleUsers.createUserAdmin();
    }

    @Test
    public void createSolutionTemplateShouldSucceed() throws Exception {
        String authToken = authenticator.loginAndGetToken(sampleUsers.getAdminLogin());
        CodeProblemResponseDto codeProblemResponseDto = initializeSampleProblem(authToken);


        SolutionTemplateDto solutionTemplateDto = new SolutionTemplateDto(
                codeProblemResponseDto.getId(),
                CodeLanguage.PYTHON,
                "def test():\n\tpass",
                "test()"
        );

        MvcResult createResult = mockMvc.perform(post("/api/code/problems/templates")
                .header("Authorization", authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(solutionTemplateDto)))
                .andExpect(status().isCreated()).andReturn();
        String location = createResult.getResponse().getHeader("Location");
        Assertions.assertNotNull(location);

        mockMvc.perform(get(location).header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.language").value("PYTHON"));
    }

    @Test
    public void createSolutionTemplateNoProblemShouldFail() throws Exception {}

    @Test
    public void editSolutionTemplateShouldSucceed() throws Exception {}

    @Test
    public void editSolutionTemplateNotOwnerShouldFail() throws Exception {}
    @Test
    public void editSolutionTemplateNoProblemShouldFail() throws Exception {}

    private CodeProblemResponseDto initializeSampleProblem(String authToken) throws Exception {
        CodeProblemCreateDto createDto = new CodeProblemCreateDto(
                "Two Sum",
                Set.of("array", "hash-table"),
                Difficulties.EASY,
                "Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target.",
                "Use a hash map to store the indices of the numbers and check for the complement."
        );
        MvcResult problemPostResult = mockMvc.perform(post("/api/code/problems")
                        .header("Authorization", authToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated()).andReturn();
        String location = problemPostResult.getResponse().getHeader("Location");
        Assertions.assertNotNull(location);
        MvcResult getResult = mockMvc.perform(get(location).header("Authorization", authToken))
                .andExpect(status().isOk()).andReturn();
        return objectMapper.readValue(getResult.getResponse().getContentAsString(), CodeProblemResponseDto.class);
    }
}
