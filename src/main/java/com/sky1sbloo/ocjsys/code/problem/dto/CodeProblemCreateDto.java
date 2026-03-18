package com.sky1sbloo.ocjsys.code.problem.dto;

import com.sky1sbloo.ocjsys.code.problem.Difficulties;
import com.sky1sbloo.ocjsys.code.problem.verifier.dto.CodeProblemVerifierCreateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CodeProblemCreateDto {
    private String title;
    private List<String> tags;
    private Difficulties difficulty;
    private String description;
    private String solution;
    @Builder.Default
    private Set<CodeProblemVerifierCreateDto> verifiers = new HashSet<>();
}
