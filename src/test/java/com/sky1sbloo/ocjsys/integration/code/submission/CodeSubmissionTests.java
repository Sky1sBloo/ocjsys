package com.sky1sbloo.ocjsys.integration.code.submission;

import com.sky1sbloo.ocjsys.code.CodeLanguage;
import com.sky1sbloo.ocjsys.code.problem.Difficulties;
import com.sky1sbloo.ocjsys.code.problem.dto.CodeProblemCreateDto;
import com.sky1sbloo.ocjsys.code.problem.dto.CodeProblemResponseDto;
import com.sky1sbloo.ocjsys.code.problem.solutiontemplate.dto.SolutionTemplateCreateDto;
import com.sky1sbloo.ocjsys.code.submission.dto.CodeSubmissionDto;
import com.sky1sbloo.ocjsys.integration.Authenticator;
import com.sky1sbloo.ocjsys.integration.auth.SampleUsers;
import org.junit.jupiter.api.Assumptions;
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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        requireDocker();
        String authToken = authenticator.loginAndGetToken(sampleUsers.getUserLogin());
        CodeProblemResponseDto problem = initSampleProblem();
        assert problem != null;
        CodeSubmissionDto codeSubmissionDto = CodeSubmissionDto.builder()
                .problemId(problem.getId())
                .sourceCode("def add(a, b):\n\treturn a+b")
                .language("python")
                .build();
        MvcResult result = mockMvc.perform(post("/api/code/submissions/run")
                        .header("Authorization", authToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(codeSubmissionDto)))
                .andExpect(status().isOk()).andReturn();
        String output = result.getResponse().getContentAsString();
        assertThat(output).contains("SUCCESS");
    }

    @Test
    void runCodeShouldFail() throws Exception {
        requireDocker();
        String authToken = authenticator.loginAndGetToken(sampleUsers.getUserLogin());
        CodeProblemResponseDto problem = initSampleProblem();
        assert problem != null;
        CodeSubmissionDto codeSubmissionDto = CodeSubmissionDto.builder()
                .problemId(problem.getId())
                .sourceCode("def add(a, b):\n\treturn a-b")
                .language("python")
                .build();
        MvcResult result = mockMvc.perform(post("/api/code/submissions/run")
                        .header("Authorization", authToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(codeSubmissionDto)))
                .andExpect(status().isOk()).andReturn();
        String output = result.getResponse().getContentAsString();
        assertThat(output).doesNotContain("SUCCESS");
    }

    @Test
    void submitCodeShouldSucceed() throws Exception {
        requireDocker();
        String authToken = authenticator.loginAndGetToken(sampleUsers.getUserLogin());
        CodeProblemResponseDto problem = initSampleProblem();
        assert problem != null;
        CodeSubmissionDto codeSubmissionDto = CodeSubmissionDto.builder()
                .problemId(problem.getId())
                .sourceCode("def add(a, b):\n\treturn a+b")
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
        requireDocker();
        String authToken = authenticator.loginAndGetToken(sampleUsers.getUserLogin());
        CodeProblemResponseDto problem = initSampleProblem();

        CodeSubmissionDto codeSubmissionDto = CodeSubmissionDto.builder()
                .problemId(problem.getId())
                .sourceCode("def add(a, b:\n\t")
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

    private CodeProblemResponseDto initSampleProblem() throws Exception {
        String adminAuthToken = authenticator.loginAndGetToken(sampleUsers.getAdminLogin());
        Set<SolutionTemplateCreateDto> solutionTemplates = new HashSet<>();
        solutionTemplates.add(new SolutionTemplateCreateDto(CodeLanguage.PYTHON,
                "def add(a, b):\n\tpass",
                "if add(1, 2) == 3:\n\tprint(\"SUCCESS\")"));
        CodeProblemCreateDto codeProblemCreateDto = CodeProblemCreateDto.builder()
                .title("Addition")
                .tags(Set.of())
                .difficulty(Difficulties.EASY)
                .description("Make a addition function")
                .solution("def add(a, b):\n\treturn a+b")
                .solutionTemplates(solutionTemplates)
                .build();
        MvcResult result = mockMvc.perform(post("/api/code/problems")
                        .header("Authorization", adminAuthToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(codeProblemCreateDto)))
                .andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(),
                CodeProblemResponseDto.class);
    }

    private void requireDocker() {
        Assumptions.assumeTrue(isDockerAvailable(), "Docker is required for code execution tests");
    }

    private boolean isDockerAvailable() {
        try {
            Process process = new ProcessBuilder("docker", "info")
                    .redirectErrorStream(true)
                    .start();
            boolean finished = process.waitFor(3, TimeUnit.SECONDS);
            return finished && process.exitValue() == 0;
        } catch (Exception ex) {
            return false;
        }
    }
}
