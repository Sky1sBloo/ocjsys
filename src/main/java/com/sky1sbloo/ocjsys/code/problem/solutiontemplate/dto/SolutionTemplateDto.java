package com.sky1sbloo.ocjsys.code.problem.solutiontemplate.dto;

import com.sky1sbloo.ocjsys.code.problem.solutiontemplate.SolutionTemplate;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SolutionTemplateDto extends SolutionTemplateGetDto {
    private String sourceCode;  // shows when loading the problem in the specified language
    private String verifierSourceCode;  // used to test the source code

    public SolutionTemplateDto(SolutionTemplate template) {
        this.language = template.getLanguage();
        this.problemId = template.getCodeProblem().getId();
        this.sourceCode = template.getSourceCode();
        this.verifierSourceCode = template.getVerifierSourceCode();
    }
}

