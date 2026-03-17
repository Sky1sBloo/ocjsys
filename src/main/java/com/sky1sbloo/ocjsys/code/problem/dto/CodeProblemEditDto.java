package com.sky1sbloo.ocjsys.code.problem.dto;

import com.sky1sbloo.ocjsys.code.problem.initialcode.dto.CodeProblemInitialDto;
import com.sky1sbloo.ocjsys.code.problem.verifier.dto.CodeProblemVerifierDto;
import lombok.Builder;

import java.util.List;

@Builder
public record CodeProblemEditDto (
        long id,
        String title,
        List<String> tags,
        String difficulty,
        String description,
        String solution,
        List<CodeProblemInitialDto> codeProblemInitialDto
){
}
