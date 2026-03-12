package com.sky1sbloo.ocjsys.code.submission;

import com.sky1sbloo.ocjsys.auth.AuthUser;
import com.sky1sbloo.ocjsys.code.problem.CodeProblemRepository;
import com.sky1sbloo.ocjsys.code.submission.dto.CodeSubmissionDto;
import com.sky1sbloo.ocjsys.runner.CodeRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class CodeSubmissionService {
    private final CodeSubmissionRepository codeSubmissionRepository;
    private final CodeProblemRepository codeProblemRepository;
    private final CodeRunner codeRunner;

    public void submitCode(CodeSubmissionDto submission, AuthUser authUser) throws IOException, InterruptedException {
            var codeSubmission = new CodeSubmission();
            codeSubmission.setProblem(codeProblemRepository.findById(submission.getProblemId()).orElse(null));
            codeSubmission.setSubmitter(authUser.getUserProfile());
            codeSubmission.setCode(submission.getCode());
            codeSubmission.setLanguage(CodeLanguage.valueOf(submission.getLanguage().toUpperCase()));
            codeRunner.runCode(codeSubmission);
            codeSubmissionRepository.save(codeSubmission);
    }
}
