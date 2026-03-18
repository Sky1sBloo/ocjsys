package com.sky1sbloo.ocjsys.code.problem.verifier.dto;

import com.sky1sbloo.ocjsys.code.CodeLanguage;

public record CodeProblemVerifierDto(
        CodeLanguage language,
        String sourceCode
) {
}
