package com.sky1sbloo.ocjsys.code.problem.solutiontemplate.dto;

import com.sky1sbloo.ocjsys.code.problem.solutiontemplate.SolutionTemplate;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
public class SolutionTemplateDto extends SolutionTemplateGetDto {
    @Getter
    @Setter
    private String sourceCode;

    public SolutionTemplateDto(SolutionTemplate template) {
        this.language = template.getLanguage();
        this.problemId = template.getCodeProblem().getId();
        this.sourceCode = template.getSourceCode();
    }
}

