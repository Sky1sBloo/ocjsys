package com.sky1sbloo.ocjsys.code.problem.dto;

import java.util.List;

public record CodeProblemCreateDto(
        String title,
        List<String> tags,
        String difficulty,
        String description,
        String solution
)
{}
