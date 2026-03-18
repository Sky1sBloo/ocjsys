package com.sky1sbloo.ocjsys.code.submission;

import com.sky1sbloo.ocjsys.auth.AuthUser;
import com.sky1sbloo.ocjsys.code.submission.dto.CodeSubmissionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.io.IOException;
import java.net.URI;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/code/submissions")
public class CodeSubmissionController {
    private final CodeSubmissionService codeSubmissionService;

    @PostMapping
    public ResponseEntity<?> submitCode(@RequestBody CodeSubmissionDto submission, @AuthenticationPrincipal AuthUser authUser) {
        CodeSubmission codeSubmission = codeSubmissionService.submitCode(submission, authUser.getUserProfile());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(codeSubmission.getId()).toUri();
        return ResponseEntity.created(location).body("Code submitted successfully");
    }

    @PostMapping("/run")
    public ResponseEntity<?> runCode(@RequestBody CodeSubmissionDto submission, @AuthenticationPrincipal AuthUser authUser) {
        String codePrint = codeSubmissionService.runCode(submission, authUser.getUserProfile());
        return ResponseEntity.ok().body(codePrint);
    }
}
