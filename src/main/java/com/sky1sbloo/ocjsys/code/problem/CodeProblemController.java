package com.sky1sbloo.ocjsys.code.problem;

import com.sky1sbloo.ocjsys.code.problem.dto.CodeProblemSearchFilterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
