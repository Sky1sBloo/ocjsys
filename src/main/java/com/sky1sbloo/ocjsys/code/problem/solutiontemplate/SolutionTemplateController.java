package com.sky1sbloo.ocjsys.code.problem.solutiontemplate;

import com.sky1sbloo.ocjsys.code.CodeLanguage;
import com.sky1sbloo.ocjsys.code.problem.solutiontemplate.dto.SolutionTemplateCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/code/problems/templates")
public class SolutionTemplateController {
    private final SolutionTemplateService solutionTemplateService;

    @GetMapping("/{problemId}")
    public ResponseEntity<?> getSolutionTemplate(@PathVariable Long problemId,
                                                 @RequestParam(required = false) CodeLanguage language) {
        if (language == null) {
            Set<SolutionTemplate> templates = solutionTemplateService.getSolutionTemplatesOfProblem(problemId);
            return ResponseEntity.ok(templates);
        }

        SolutionTemplate template = solutionTemplateService.getSolutionTemplateOfProblem(problemId, language);
        return ResponseEntity.ok(template);
    }

    @PostMapping
    public ResponseEntity<?> addSolutionTemplate(@RequestBody SolutionTemplateCreateDto solutionTemplateCreateDto) {
        solutionTemplateService.addSolutionTemplate(solutionTemplateCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
