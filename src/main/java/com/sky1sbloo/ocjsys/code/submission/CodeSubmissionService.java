package com.sky1sbloo.ocjsys.code.submission;

import com.sky1sbloo.ocjsys.auth.AuthUser;
import com.sky1sbloo.ocjsys.code.problem.CodeProblemRepository;
import com.sky1sbloo.ocjsys.code.submission.dto.CodeSubmissionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CodeSubmissionService {
    private final CodeSubmissionRepository codeSubmissionRepository;
    private final CodeProblemRepository codeProblemRepository;

    public void submitCode(CodeSubmissionDto submission, AuthUser authUser) {
            var codeSubmission = new CodeSubmission();
            codeSubmission.setProblem(codeProblemRepository.findById(submission.getProblemId()).orElse(null));
            codeSubmission.setSubmitter(authUser.getUserProfile());
            codeSubmission.setCode(submission.getCode());
            codeSubmission.setLanguage(CodeLanguage.valueOf(submission.getLanguage().toUpperCase()));
            codeSubmissionRepository.save(codeSubmission);
    }
}
