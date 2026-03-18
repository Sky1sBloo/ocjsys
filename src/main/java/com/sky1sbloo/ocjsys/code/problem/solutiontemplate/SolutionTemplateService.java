package com.sky1sbloo.ocjsys.code.problem.solutiontemplate;

import com.sky1sbloo.ocjsys.code.CodeLanguage;
import com.sky1sbloo.ocjsys.code.problem.CodeProblem;
import com.sky1sbloo.ocjsys.code.problem.CodeProblemRepository;
import com.sky1sbloo.ocjsys.code.problem.solutiontemplate.dto.SolutionTemplateDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class SolutionTemplateService {
    private final CodeProblemRepository codeProblemRepository;
    private final SolutionTemplateRepository solutionTemplateRepository;

    public void addSolutionTemplate(SolutionTemplateDto solutionTemplateDto) {
        SolutionTemplate solutionTemplate = createSolutionTemplateFromDto(solutionTemplateDto);
        solutionTemplateRepository.save(solutionTemplate);
    }

    public void editSolutionTemplate(SolutionTemplateDto solutionTemplateDto) {
        SolutionTemplate solutionTemplate = createSolutionTemplateFromDto(solutionTemplateDto);
        solutionTemplateRepository.save(solutionTemplate);
    }

    public void deleteSolutionTemplate(SolutionTemplateDto solutionTemplateDto) {
        SolutionTemplateId templateId = createTemplateIdFromDto(solutionTemplateDto);
        solutionTemplateRepository.deleteById(templateId);
    }

    private SolutionTemplate createSolutionTemplateFromDto(SolutionTemplateDto solutionTemplateDto)
            throws EntityNotFoundException, IllegalArgumentException {
        CodeProblem problem = codeProblemRepository.findById(solutionTemplateDto.problemId()).orElseThrow(
                () -> new EntityNotFoundException("Code problem with id: " + solutionTemplateDto.problemId() +
                        " not found")
        );
        var language = CodeLanguage.valueOf(solutionTemplateDto.language());
        return SolutionTemplate.builder()
                .codeProblem(problem)
                .language(language)
                .sourceCode(solutionTemplateDto.sourceCode()).build();
    }

    private SolutionTemplateId createTemplateIdFromDto(SolutionTemplateDto solutionTemplateDto)
            throws EntityNotFoundException, IllegalArgumentException {
        CodeProblem problem = codeProblemRepository.findById(solutionTemplateDto.problemId()).orElseThrow(
                () -> new EntityNotFoundException("Code problem with id: " + solutionTemplateDto.problemId() +
                        " not found")
        );
        var language = CodeLanguage.valueOf(solutionTemplateDto.language());
        return new SolutionTemplateId(problem, language);
    }
}
