package com.sky1sbloo.ocjsys.code.problem.solutiontemplate.dto;

import com.sky1sbloo.ocjsys.code.CodeLanguage;
import com.sky1sbloo.ocjsys.code.problem.solutiontemplate.SolutionTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SolutionTemplateEditDto {
    protected CodeLanguage language;
    private String sourceCode;  // shows when loading the problem in the specified language
    private String verifierSourceCode;  // used to test the source code

    public SolutionTemplateEditDto(Long problemId,
                               CodeLanguage language,
                               String sourceCode,
                               String verifierSourceCode) {
        this.language = language;
        this.sourceCode = sourceCode;
        this.verifierSourceCode = verifierSourceCode;
    }

    public SolutionTemplateEditDto(SolutionTemplate template) {
        this.language = template.getLanguage();
        this.sourceCode = template.getSourceCode();
        this.verifierSourceCode = template.getVerifierSourceCode();
    }
}
