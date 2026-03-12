package com.sky1sbloo.ocjsys.code.problem.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record CodeProblemCreateDto(
        String title,
        List<String> tags,
        String difficulty,
        String description,
        String solution
)
{}
