package com.sky1sbloo.ocjsys.code.problem.solutiontemplate;

import com.sky1sbloo.ocjsys.code.problem.solutiontemplate.dto.SolutionTemplateDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class SolutionTemplateController {
    private final SolutionTemplateService solutionTemplateService;

    @PostMapping
    public ResponseEntity<?> addSolutionTemplate(@RequestBody SolutionTemplateDto solutionTemplateDto) {
        try {
            solutionTemplateService.addSolutionTemplate(solutionTemplateDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Unknown language");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
