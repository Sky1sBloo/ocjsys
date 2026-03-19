package com.sky1sbloo.ocjsys.code.problem.solutiontemplate;

import com.sky1sbloo.ocjsys.code.CodeLanguage;
import com.sky1sbloo.ocjsys.code.problem.solutiontemplate.dto.SolutionTemplateDto;
import com.sky1sbloo.ocjsys.code.problem.solutiontemplate.dto.SolutionTemplateEditDto;
import com.sky1sbloo.ocjsys.code.problem.solutiontemplate.dto.SolutionTemplateGetDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
    @PreAuthorize("hasAuthority('CREATE_CODE_PROBLEMS')")
    public ResponseEntity<Void> addSolutionTemplate(@RequestBody SolutionTemplateDto solutionTemplateCreateDto) {
        SolutionTemplate template = solutionTemplateService.addSolutionTemplate(solutionTemplateCreateDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .queryParam("language", template.getLanguage())
                .buildAndExpand(template.getCodeProblem().getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{problemId}")
    @PreAuthorize("hasAuthority('CREATE_CODE_PROBLEMS')")
    public ResponseEntity<Void> updateSolutionTemplate(
            @PathVariable Long problemId,
            @RequestBody SolutionTemplateEditDto solutionTemplateEditDto) {
        solutionTemplateService.editSolutionTemplate(problemId, solutionTemplateEditDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('CREATE_CODE_PROBLEMS')")
    public ResponseEntity<Void> deleteSolutionTemplate(SolutionTemplateGetDto solutionTemplateGetDto) {
        solutionTemplateService.deleteSolutionTemplate(solutionTemplateGetDto);
        return ResponseEntity.noContent().build();
    }
}
