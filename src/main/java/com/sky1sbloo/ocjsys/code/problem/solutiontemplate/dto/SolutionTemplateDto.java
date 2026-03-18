package com.sky1sbloo.ocjsys.code.problem.solutiontemplate.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
public class SolutionTemplateDto extends SolutionTemplateGetDto {
    @Getter
    @Setter
    private String sourceCode;
}

