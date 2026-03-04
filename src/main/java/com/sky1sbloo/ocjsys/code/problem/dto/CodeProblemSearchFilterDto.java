package com.sky1sbloo.ocjsys.code.problem.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record CodeProblemSearchFilterDto(
        String ownerName,
        String title,
        List<String> tags,
        List<String> difficulties
) {
}
