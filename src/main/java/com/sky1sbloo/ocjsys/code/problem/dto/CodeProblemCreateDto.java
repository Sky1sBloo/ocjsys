package com.sky1sbloo.ocjsys.code.problem.dto;

import com.sky1sbloo.ocjsys.code.problem.Difficulties;
import com.sky1sbloo.ocjsys.code.problem.solutiontemplate.dto.SolutionTemplateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CodeProblemCreateDto {
    private String title;
    private Set<String> tags;
    private Difficulties difficulty;
    private String description;
    private String solution;
    @Builder.Default
    private Set<SolutionTemplateDto> solutionTemplates = new HashSet<>();

    public CodeProblemCreateDto(
            String title,
            Set<String> tags,
            Difficulties difficulty,
            String description,
            String solution
    ) {
        this.title = title;
        this.tags = tags;
        this.difficulty = difficulty;
        this.description = description;
        this.solution = solution;
    }
}
