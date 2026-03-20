package com.sky1sbloo.ocjsys.code.problem.solutiontemplate;

import com.sky1sbloo.ocjsys.auth.AuthUser;
import com.sky1sbloo.ocjsys.code.CodeLanguage;
import com.sky1sbloo.ocjsys.code.problem.CodeProblem;
import com.sky1sbloo.ocjsys.code.problem.CodeProblemRepository;
import com.sky1sbloo.ocjsys.code.problem.solutiontemplate.dto.SolutionTemplateDto;
import com.sky1sbloo.ocjsys.code.problem.solutiontemplate.dto.SolutionTemplateEditDto;
import com.sky1sbloo.ocjsys.code.problem.solutiontemplate.dto.SolutionTemplateGetDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@RequiredArgsConstructor
@Transactional
@Service
public class SolutionTemplateService {
    private final CodeProblemRepository codeProblemRepository;
    private final SolutionTemplateRepository solutionTemplateRepository;

    public SolutionTemplate getSolutionTemplateById(SolutionTemplateGetDto solutionTemplateGetDto) {
        SolutionTemplateId templateId = createTemplateIdFromDto(solutionTemplateGetDto);
        return solutionTemplateRepository.findById(templateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Set<SolutionTemplate> getSolutionTemplatesOfProblem(Long problemId)
            throws EntityNotFoundException {
        return solutionTemplateRepository.findAllByCodeProblem_Id(problemId);
    }

    public SolutionTemplate getSolutionTemplateOfProblem(Long problemId, CodeLanguage language) {
        return solutionTemplateRepository.findByCodeProblem_IdAndLanguage(problemId, language);
    }

    public SolutionTemplate addSolutionTemplate(SolutionTemplateDto solutionTemplateCreateDto, AuthUser user)
            throws AccessDeniedException {
        SolutionTemplate solutionTemplate = createSolutionTemplateFromDto(solutionTemplateCreateDto);
        if (!userOwnsCodeProblem(user, solutionTemplate.getCodeProblem())) {
            throw new AccessDeniedException("User do not own this code problem");
        }
        return solutionTemplateRepository.save(solutionTemplate);
    }

    public void editSolutionTemplate(Long id, SolutionTemplateEditDto solutionTemplateEditDto, AuthUser user)
            throws AccessDeniedException {
        CodeProblem problem = codeProblemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Code problem with id: " + id +
                        " not found")
        );
        if (!userOwnsCodeProblem(user, problem)) {
            throw new AccessDeniedException("User do not own this code problem");
        }
        SolutionTemplate solutionTemplate = SolutionTemplate.builder()
                .codeProblem(problem)
                .language(solutionTemplateEditDto.getLanguage())
                .sourceCode(solutionTemplateEditDto.getSourceCode())
                .verifierSourceCode(solutionTemplateEditDto.getVerifierSourceCode())
                .build();
        solutionTemplateRepository.save(solutionTemplate);
    }

    public void deleteSolutionTemplate(SolutionTemplateGetDto solutionTemplateGetDto, AuthUser user)
            throws AccessDeniedException {
        SolutionTemplateId templateId = createTemplateIdFromDto(solutionTemplateGetDto);
        CodeProblem problem = codeProblemRepository.findById(solutionTemplateGetDto.getProblemId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Code problem with id: " + solutionTemplateGetDto.getProblemId() +
                        " not found")
        );
        if (!userOwnsCodeProblem(user, problem)) {
            throw new AccessDeniedException("User do not own this code problem");
        }
        solutionTemplateRepository.deleteById(templateId);
    }

    private Boolean userOwnsCodeProblem(AuthUser user, CodeProblem codeProblem) {
        // TODO: check if user is admin and allow ownership
        return codeProblem.getOwner().getAuthUser().getUsername().equals(user.getUsername());
    }

    private SolutionTemplate createSolutionTemplateFromDto(SolutionTemplateDto solutionTemplateCreateDto)
            throws IllegalArgumentException {
        CodeProblem problem = codeProblemRepository.findById(solutionTemplateCreateDto.getProblemId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        solutionTemplateCreateDto.getProblemId() + " not found")
        );
        return SolutionTemplate.builder()
                .codeProblem(problem)
                .language(solutionTemplateCreateDto.getLanguage())
                .sourceCode(solutionTemplateCreateDto.getSourceCode())
                .verifierSourceCode(solutionTemplateCreateDto.getVerifierSourceCode())
                .build();
    }

    private SolutionTemplateId createTemplateIdFromDto(SolutionTemplateGetDto solutionTemplateGetDto)
            throws IllegalArgumentException {
        CodeProblem problem = codeProblemRepository.findById(solutionTemplateGetDto.getProblemId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        solutionTemplateGetDto.getProblemId() + " not found"));
        return new SolutionTemplateId(problem, solutionTemplateGetDto.getLanguage());
    }
}
