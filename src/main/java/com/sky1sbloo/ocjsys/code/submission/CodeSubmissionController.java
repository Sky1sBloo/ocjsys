package com.sky1sbloo.ocjsys.code.submission;

import com.sky1sbloo.ocjsys.auth.AuthUser;
import com.sky1sbloo.ocjsys.code.submission.dto.CodeSubmissionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/code/submissions")
public class CodeSubmissionController {
    private final CodeSubmissionService codeSubmissionService;

    @PostMapping
    public ResponseEntity<?> submitCode(@RequestBody CodeSubmissionDto submission, @AuthenticationPrincipal AuthUser authUser) {
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
        return ResponseEntity.status(HttpStatus.CREATED).body("Code submitted successfully");
    }

    @PostMapping("/run")
    public ResponseEntity<?> runCode(@RequestBody CodeSubmissionDto submission, @AuthenticationPrincipal AuthUser authUser) {
        try {
            String codePrint = codeSubmissionService.runCode(submission, authUser.getUserProfile());
            return ResponseEntity.ok().body(codePrint);
        } catch (IOException | IllegalArgumentException ex) {
            log.error(ex.getMessage(), ex);
            return ResponseEntity.badRequest().body("Error executing code");
        } catch (InterruptedException ex) {
            log.error(ex.getMessage(), ex);
            Thread.currentThread().interrupt();
            return ResponseEntity.badRequest().body("Error executing code");
        }
    }
}
