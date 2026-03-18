package com.sky1sbloo.ocjsys.code.problem.solutiontemplate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolutionTemplateGetDto {
    private Long problemId;
    private String language;
}
