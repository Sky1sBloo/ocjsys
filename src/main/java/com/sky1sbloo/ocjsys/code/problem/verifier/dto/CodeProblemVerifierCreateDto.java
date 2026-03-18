package com.sky1sbloo.ocjsys.code.problem.verifier.dto;

import com.sky1sbloo.ocjsys.code.CodeLanguage;

public record CodeProblemVerifierCreateDto(
        CodeLanguage language,
        String sourceCode
) {
}
