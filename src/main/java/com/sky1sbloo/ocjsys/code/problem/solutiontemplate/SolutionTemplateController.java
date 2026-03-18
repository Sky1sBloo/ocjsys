package com.sky1sbloo.ocjsys.code.problem.solutiontemplate;

import com.sky1sbloo.ocjsys.code.problem.solutiontemplate.dto.SolutionTemplateCreateDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class SolutionTemplateController {
    private final SolutionTemplateService solutionTemplateService;

    @GetMapping
    public ResponseEntity<List<SolutionTemplate>> getAllSolutionTemplates() {

    }

    @PostMapping
    public ResponseEntity<?> addSolutionTemplate(@RequestBody SolutionTemplateCreateDto solutionTemplateCreateDto) {
        try {
            solutionTemplateService.addSolutionTemplate(solutionTemplateCreateDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Unknown language");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
