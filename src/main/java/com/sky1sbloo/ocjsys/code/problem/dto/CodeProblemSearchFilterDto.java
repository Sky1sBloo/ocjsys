package com.sky1sbloo.ocjsys.code.problem.dto;

import lombok.Builder;

import java.util.List;
import java.util.Set;

@Builder
public record CodeProblemSearchFilterDto(
        String ownerName,
        String title,
        List<String> tags,
        Set<String> difficulties
) {
}
