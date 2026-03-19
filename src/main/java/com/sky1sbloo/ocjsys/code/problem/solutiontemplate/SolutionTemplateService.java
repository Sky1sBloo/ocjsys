package com.sky1sbloo.ocjsys.code.problem.solutiontemplate;

import com.sky1sbloo.ocjsys.code.CodeLanguage;
import com.sky1sbloo.ocjsys.code.problem.CodeProblem;
import com.sky1sbloo.ocjsys.code.problem.CodeProblemRepository;
import com.sky1sbloo.ocjsys.code.problem.solutiontemplate.dto.SolutionTemplateDto;
import com.sky1sbloo.ocjsys.code.problem.solutiontemplate.dto.SolutionTemplateEditDto;
import com.sky1sbloo.ocjsys.code.problem.solutiontemplate.dto.SolutionTemplateGetDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@RequiredArgsConstructor
@Transactional
@Service
public class SolutionTemplateService {
    private final CodeProblemRepository codeProblemRepository;
    private final SolutionTemplateRepository solutionTemplateRepository;

    public SolutionTemplate getSolutionTemplateById(SolutionTemplateGetDto solutionTemplateGetDto)
            throws EntityNotFoundException {
        SolutionTemplateId templateId = createTemplateIdFromDto(solutionTemplateGetDto);
        return solutionTemplateRepository.findById(templateId)
                .orElseThrow(() -> new EntityNotFoundException(templateId.toString()));
    }

    public Set<SolutionTemplate> getSolutionTemplatesOfProblem(Long problemId)
            throws EntityNotFoundException {
        return solutionTemplateRepository.findAllByCodeProblem_Id(problemId);
    }

    public SolutionTemplate getSolutionTemplateOfProblem(Long problemId, CodeLanguage language) {
        return solutionTemplateRepository.findByCodeProblem_IdAndLanguage(problemId, language);
    }

    public SolutionTemplate addSolutionTemplate(SolutionTemplateDto solutionTemplateCreateDto) {
        SolutionTemplate solutionTemplate = createSolutionTemplateFromDto(solutionTemplateCreateDto);
        return solutionTemplateRepository.save(solutionTemplate);
    }

    public void editSolutionTemplate(Long id, SolutionTemplateEditDto solutionTemplateEditDto) {
        CodeProblem problem = codeProblemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Code problem with id: " + id +
                        " not found")
        );
        SolutionTemplate solutionTemplate = SolutionTemplate.builder()
                .codeProblem(problem)
                .language(solutionTemplateEditDto.getLanguage())
                .sourceCode(solutionTemplateEditDto.getSourceCode())
                .verifierSourceCode(solutionTemplateEditDto.getVerifierSourceCode())
                .build();
        solutionTemplateRepository.save(solutionTemplate);
    }

    public void deleteSolutionTemplate(SolutionTemplateGetDto solutionTemplateGetDto) {
        SolutionTemplateId templateId = createTemplateIdFromDto(solutionTemplateGetDto);
        solutionTemplateRepository.deleteById(templateId);
    }

    private SolutionTemplate createSolutionTemplateFromDto(SolutionTemplateDto solutionTemplateCreateDto)
            throws EntityNotFoundException, IllegalArgumentException {
        CodeProblem problem = codeProblemRepository.findById(solutionTemplateCreateDto.getProblemId()).orElseThrow(
                () -> new EntityNotFoundException("Code problem with id: " + solutionTemplateCreateDto.getProblemId() +
                        " not found")
        );
        return SolutionTemplate.builder()
                .codeProblem(problem)
                .language(solutionTemplateCreateDto.getLanguage())
                .sourceCode(solutionTemplateCreateDto.getSourceCode())
                .verifierSourceCode(solutionTemplateCreateDto.getVerifierSourceCode())
                .build();
    }

    private SolutionTemplateId createTemplateIdFromDto(SolutionTemplateGetDto solutionTemplateGetDto)
            throws EntityNotFoundException, IllegalArgumentException {
        CodeProblem problem = codeProblemRepository.findById(solutionTemplateGetDto.getProblemId()).orElseThrow(
                () -> new EntityNotFoundException("Code problem with id: " + solutionTemplateGetDto.getProblemId() +
                        " not found")
        );
        return new SolutionTemplateId(problem, solutionTemplateGetDto.getLanguage());
    }
}
