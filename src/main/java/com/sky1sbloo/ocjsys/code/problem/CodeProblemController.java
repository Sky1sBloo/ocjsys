package com.sky1sbloo.ocjsys.code.problem;

import com.sky1sbloo.ocjsys.auth.AuthUser;
import com.sky1sbloo.ocjsys.code.problem.dto.CodeProblemCreateDto;
import com.sky1sbloo.ocjsys.code.problem.dto.CodeProblemSearchFilterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/code/problems")
public class CodeProblemController {
    private final CodeProblemService codeProblemService;

    @GetMapping("/")
    public ResponseEntity<List<CodeProblem>> getProblems(
            @RequestParam String owner,
            @RequestParam String title,
            @RequestParam List<String> tags,
            @RequestParam List<String> difficulties
    ) {
        var codeProblemDto = CodeProblemSearchFilterDto.builder()
                .ownerName(owner)
                .title(title)
                .tags(tags)
                .difficulties(difficulties)
                .build();
        try {
            var filter = codeProblemService.convertToFilter(codeProblemDto);
            var problems = codeProblemService.findProblems(filter);
            return ResponseEntity.ok(problems);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    @PreAuthorize("#principal.username==#authUser.username && hasAuthority('CREATE_CODE_PROBLEMS')")
    public ResponseEntity<CodeProblem> createProblem(
            @RequestBody CodeProblemCreateDto codeProblem,
            @AuthenticationPrincipal AuthUser authUser) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            codeProblemService.createProblem(codeProblem, authUser);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.noContent().build();
    }
}
