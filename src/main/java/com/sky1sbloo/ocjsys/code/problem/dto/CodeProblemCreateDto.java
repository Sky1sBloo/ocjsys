package com.sky1sbloo.ocjsys.code.problem.dto;

import com.sky1sbloo.ocjsys.code.problem.solver.dto.CodeProblemSolverDto;
import lombok.Builder;

import java.util.List;

@Builder
public record CodeProblemCreateDto(
        String title,
        List<String> tags,
        String difficulty,
        String description,
        String solution,
        List<CodeProblemSolverDto> codeSolvers
)
{}
