package com.sky1sbloo.ocjsys.code.problem.initialcode.dto;

import lombok.Builder;

@Builder
public record CodeProblemInitialDto(
        String language,
        String sourceCode
) {
}
