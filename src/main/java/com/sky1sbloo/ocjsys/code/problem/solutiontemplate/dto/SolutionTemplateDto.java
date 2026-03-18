package com.sky1sbloo.ocjsys.code.problem.solutiontemplate.dto;

import lombok.Builder;

@Builder
public record SolutionTemplateDto(
        Long problemId,
        String language,
        String sourceCode
) {
}
