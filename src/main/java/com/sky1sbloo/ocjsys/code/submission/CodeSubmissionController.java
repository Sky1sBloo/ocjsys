package com.sky1sbloo.ocjsys.code.submission;

import com.sky1sbloo.ocjsys.auth.AuthUser;
import com.sky1sbloo.ocjsys.code.submission.dto.CodeSubmissionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/code/submissions")
public class CodeSubmissionController {
    private final CodeSubmissionService codeSubmissionService;

    @PostMapping
    public ResponseEntity<?> submitCode(CodeSubmissionDto submission, @AuthenticationPrincipal AuthUser authUser) {
        try {
            codeSubmissionService.submitCode(submission, authUser.getUserProfile());
        } catch (IOException | IllegalArgumentException ex) {
            log.error(ex.getMessage(), ex);
            return ResponseEntity.badRequest().body("Error executing code");
        } catch (InterruptedException ex) {
            log.error(ex.getMessage(), ex);
            Thread.currentThread().interrupt();
            return ResponseEntity.badRequest().body("Error executing code");
        }
        return ResponseEntity.ok().body("Code submitted successfully");
    }

    @PostMapping("/run")
    public ResponseEntity<?> runCode(CodeSubmissionDto submission, @AuthenticationPrincipal AuthUser authUser) {
        try {
            codeSubmissionService.runCode(submission, authUser.getUserProfile());
        } catch (IOException | IllegalArgumentException ex) {
            log.error(ex.getMessage(), ex);
            return ResponseEntity.badRequest().body("Error executing code");
        } catch (InterruptedException ex) {
            log.error(ex.getMessage(), ex);
            Thread.currentThread().interrupt();
            return ResponseEntity.badRequest().body("Error executing code");
        }
        return ResponseEntity.ok().body("Code submitted successfully");
    }
}
