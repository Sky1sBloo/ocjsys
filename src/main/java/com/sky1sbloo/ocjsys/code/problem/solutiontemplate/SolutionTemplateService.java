package com.sky1sbloo.ocjsys.code.problem.solutiontemplate;

import com.sky1sbloo.ocjsys.code.CodeLanguage;
import com.sky1sbloo.ocjsys.code.problem.CodeProblem;
import com.sky1sbloo.ocjsys.code.problem.CodeProblemRepository;
import com.sky1sbloo.ocjsys.code.problem.solutiontemplate.dto.SolutionTemplateCreateDto;
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
        return codeProblemRepository.findById(problemId)
                .orElseThrow(() -> new EntityNotFoundException("Cannot find problem: " + problemId))
                .getSolutionTemplates();
    }

    public void addSolutionTemplate(SolutionTemplateCreateDto solutionTemplateCreateDto) {
        SolutionTemplate solutionTemplate = createSolutionTemplateFromDto(solutionTemplateCreateDto);
        solutionTemplateRepository.save(solutionTemplate);
    }

    public void editSolutionTemplate(SolutionTemplateCreateDto solutionTemplateCreateDto) {
        SolutionTemplate solutionTemplate = createSolutionTemplateFromDto(solutionTemplateCreateDto);
        solutionTemplateRepository.save(solutionTemplate);
    }

    public void deleteSolutionTemplate(SolutionTemplateCreateDto solutionTemplateCreateDto) {
        SolutionTemplateId templateId = createTemplateIdFromDto(solutionTemplateCreateDto);
        solutionTemplateRepository.deleteById(templateId);
    }

    private SolutionTemplate createSolutionTemplateFromDto(SolutionTemplateCreateDto solutionTemplateCreateDto)
            throws EntityNotFoundException, IllegalArgumentException {
        CodeProblem problem = codeProblemRepository.findById(solutionTemplateCreateDto.getProblemId()).orElseThrow(
                () -> new EntityNotFoundException("Code problem with id: " + solutionTemplateCreateDto.getProblemId() +
                        " not found")
        );
        var language = CodeLanguage.valueOf(solutionTemplateCreateDto.getLanguage());
        return SolutionTemplate.builder()
                .codeProblem(problem)
                .language(language)
                .sourceCode(solutionTemplateCreateDto.getSourceCode()).build();
    }

    private SolutionTemplateId createTemplateIdFromDto(SolutionTemplateGetDto solutionTemplateCreateDto)
            throws EntityNotFoundException, IllegalArgumentException {
        CodeProblem problem = codeProblemRepository.findById(solutionTemplateCreateDto.getProblemId()).orElseThrow(
                () -> new EntityNotFoundException("Code problem with id: " + solutionTemplateCreateDto.getProblemId() +
                        " not found")
        );
        var language = CodeLanguage.valueOf(solutionTemplateCreateDto.getLanguage());
        return new SolutionTemplateId(problem, language);
    }
}
