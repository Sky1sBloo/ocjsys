package com.sky1sbloo.ocjsys.code.problem;

import com.sky1sbloo.ocjsys.auth.AuthUser;
import com.sky1sbloo.ocjsys.code.problem.dto.CodeProblemCreateDto;
import com.sky1sbloo.ocjsys.code.problem.dto.CodeProblemEditDto;
import com.sky1sbloo.ocjsys.code.problem.dto.CodeProblemResponseDto;
import com.sky1sbloo.ocjsys.code.problem.dto.CodeProblemSearchFilterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/code/problems")
public class CodeProblemController {
    private final CodeProblemService codeProblemService;

    @GetMapping
    public ResponseEntity<Set<CodeProblemResponseDto>> getProblems(
            @RequestParam(required = false) String owner,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) Set<String> difficulties
    ) {
        var codeProblemDto = CodeProblemSearchFilterDto.builder()
                .ownerName(owner)
                .title(title)
                .tags(tags)
                .difficulties(difficulties)
                .build();
        var filter = codeProblemService.convertToFilter(codeProblemDto);
        var problems = codeProblemService.findProblems(filter);
        Set<CodeProblemResponseDto> response = new HashSet<>();
        for (CodeProblem problem : problems) {
            response.add(new CodeProblemResponseDto(problem));
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @RequestMapping("/{id}")
    public ResponseEntity<CodeProblemResponseDto> getProblem(@PathVariable Long id) {
        CodeProblem codeProblem = codeProblemService.findProblem(id);
        return ResponseEntity.ok(new CodeProblemResponseDto(codeProblem));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_CODE_PROBLEMS')")
    public ResponseEntity<CodeProblemResponseDto> createProblem(
            @RequestBody CodeProblemCreateDto codeProblem,
            @AuthenticationPrincipal AuthUser authUser) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            CodeProblem problem = codeProblemService.createProblem(codeProblem, authUser);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(problem.getId()).toUri();
            return ResponseEntity.created(location).body(new CodeProblemResponseDto(problem));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping
    @PreAuthorize("hasAuthority('CREATE_CODE_PROBLEMS')")
    public ResponseEntity<CodeProblem> updateProblem(
            @RequestBody CodeProblemEditDto codeProblem,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        if (authUser == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            CodeProblem problem = codeProblemService.editProblem(codeProblem, authUser);
            return ResponseEntity.ok().body(problem);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
