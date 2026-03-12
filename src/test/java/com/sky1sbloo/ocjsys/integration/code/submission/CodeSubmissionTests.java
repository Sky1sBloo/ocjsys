package com.sky1sbloo.ocjsys.integration.code.submission;

import com.sky1sbloo.ocjsys.code.problem.CodeProblem;
import com.sky1sbloo.ocjsys.code.problem.dto.CodeProblemCreateDto;
import com.sky1sbloo.ocjsys.code.submission.dto.CodeSubmissionDto;
import com.sky1sbloo.ocjsys.integration.Authenticator;
import com.sky1sbloo.ocjsys.integration.auth.SampleUsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CodeSubmissionTests {
    private final SampleUsers sampleUsers;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final Authenticator authenticator;

    @Autowired
    public CodeSubmissionTests(SampleUsers sampleUsers, MockMvc mockMvc, ObjectMapper objectMapper, Authenticator authenticator) {
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
    void runCodeShouldSucceed() throws Exception {
        String authToken = authenticator.loginAndGetToken(sampleUsers.getUserLogin());
        String adminAuthToken = authenticator.loginAndGetToken(sampleUsers.getAdminLogin());
        CodeProblemCreateDto codeProblemCreateDto = CodeProblemCreateDto.builder()
                .title("Hello world")
                .tags(List.of())
                .difficulty("EASY")
                .description("Build hello world")
                .solution("print('Hello, World!')")
                .build();
        MvcResult result = mockMvc.perform(post("/api/code/problems")
                        .header("Authorization", adminAuthToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(codeProblemCreateDto)))
                .andReturn();
        CodeProblem problem = objectMapper.readValue(result.getResponse().getContentAsString(), CodeProblem.class);
        assert problem != null;
        CodeSubmissionDto codeSubmissionDto = CodeSubmissionDto.builder()
                .problemId(problem.getId())
                .sourceCode("print('Hello, World!')")
                .language("python")
                .build();
        result = mockMvc.perform(post("/api/code/submissions/run")
                        .header("Authorization", authToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(codeSubmissionDto)))
                .andExpect(status().isOk()).andReturn();
        String output = result.getResponse().getContentAsString();
        assertThat(output).contains("Hello, World!");
    }

    @Test
    void submitCodeShouldSucceed() throws Exception {
        String authToken = authenticator.loginAndGetToken(sampleUsers.getUserLogin());
        String adminAuthToken = authenticator.loginAndGetToken(sampleUsers.getAdminLogin());
        CodeProblemCreateDto codeProblemCreateDto = CodeProblemCreateDto.builder()
                .title("Hello world")
                .tags(List.of())
                .difficulty("EASY")
                .description("Build hello world")
                .solution("print('Hello, World!')")
                .build();
        MvcResult result = mockMvc.perform(post("/api/code/problems")
                        .header("Authorization", adminAuthToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(codeProblemCreateDto)))
                .andReturn();
        CodeProblem problem = objectMapper.readValue(result.getResponse().getContentAsString(), CodeProblem.class);
        assert problem != null;
        CodeSubmissionDto codeSubmissionDto = CodeSubmissionDto.builder()
                .problemId(problem.getId())
                .sourceCode("print('Hello, World!')")
                .language("python")
                .build();
        mockMvc.perform(post("/api/code/submissions")
                        .header("Authorization", authToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(codeSubmissionDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void runCodeShouldFailWithInvalidCode() throws Exception {
        String authToken = authenticator.loginAndGetToken(sampleUsers.getUserLogin());
        CodeSubmissionDto codeSubmissionDto = CodeSubmissionDto.builder()
                .problemId(1L)
                .sourceCode("print('Hello, World!'")
                .language("python")
                .build();
        mockMvc.perform(post("/api/code/submissions/run")
                        .header("Authorization", authToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(codeSubmissionDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void runCodeShouldFailWithUnsupportedLanguage() throws Exception {
        String authToken = authenticator.loginAndGetToken(sampleUsers.getUserLogin());
        CodeSubmissionDto codeSubmissionDto = CodeSubmissionDto.builder()
                .problemId(1L)
                .sourceCode("print('Hello, World!')")
                .language("randomLanguage")
                .build();
        mockMvc.perform(post("/api/code/submissions/run")
                        .header("Authorization", authToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(codeSubmissionDto)))
                .andExpect(status().isBadRequest());
    }
}
