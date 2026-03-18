package com.sky1sbloo.ocjsys.code.problem.solutiontemplate.dto;

import com.sky1sbloo.ocjsys.code.CodeLanguage;
import com.sky1sbloo.ocjsys.code.problem.solutiontemplate.SolutionTemplate;
import lombok.*;

/**
 * Used for manually adding or editing solution templates
 * Different from {@link SolutionTemplateCreateDto} where it is used by initial code problem creation
 */
@Getter
@Setter
public class SolutionTemplateDto {
    protected Long problemId;
    protected CodeLanguage language;
    private String sourceCode;  // shows when loading the problem in the specified language
    private String verifierSourceCode;  // used to test the source code

    public SolutionTemplateDto(Long problemId,
                               CodeLanguage language,
                               String sourceCode,
                               String verifierSourceCode) {
        this.problemId = problemId;
        this.language = language;
        this.sourceCode = sourceCode;
        this.verifierSourceCode = verifierSourceCode;
    }

    public SolutionTemplateDto(SolutionTemplate template) {
        this.language = template.getLanguage();
        this.problemId = template.getCodeProblem().getId();
        this.sourceCode = template.getSourceCode();
        this.verifierSourceCode = template.getVerifierSourceCode();
    }
}

