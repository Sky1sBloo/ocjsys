package com.sky1sbloo.ocjsys.code.submission;

import com.sky1sbloo.ocjsys.code.problem.CodeProblemRepository;
import com.sky1sbloo.ocjsys.code.submission.dto.CodeSubmissionDto;
import com.sky1sbloo.ocjsys.runner.CodeRunner;
import com.sky1sbloo.ocjsys.userprofile.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class CodeSubmissionService {
    private final CodeSubmissionRepository codeSubmissionRepository;
    private final CodeProblemRepository codeProblemRepository;
    private final CodeRunner codeRunner;

    public CodeSubmission submitCode(CodeSubmissionDto submission, UserProfile userProfile) throws IOException, InterruptedException {
        var codeSubmission = createCodeSubmission(submission, userProfile);
        codeRunner.runCode(codeSubmission);
        return codeSubmissionRepository.save(codeSubmission);
    }

    public String runCode(CodeSubmissionDto submission, UserProfile userProfile) throws IOException, InterruptedException {
        var codeSubmission = createCodeSubmission(submission, userProfile);
        return codeRunner.runCode(codeSubmission);
    }

    private CodeSubmission createCodeSubmission(CodeSubmissionDto submission, UserProfile userProfile) {
        var codeSubmission = new CodeSubmission();
        codeSubmission.setProblem(codeProblemRepository.findById(submission.getProblemId()).orElseThrow(
                () -> new IllegalArgumentException("Problem not found")
        ));
        codeSubmission.setSubmitter(userProfile);
        codeSubmission.setCode(submission.getSourceCode());
        codeSubmission.setLanguage(CodeLanguage.valueOf(submission.getLanguage().toUpperCase()));
        return codeSubmission;
    }
}
