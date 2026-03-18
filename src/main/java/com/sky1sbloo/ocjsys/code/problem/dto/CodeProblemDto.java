package com.sky1sbloo.ocjsys.code.problem.dto;

import com.sky1sbloo.ocjsys.code.problem.CodeProblem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CodeProblemDto {
    private Long id;
    private String ownerName;
    private String title;
    private String difficulty;
    private Set<String> tags;
    private String description;
    private String solution;

    public CodeProblemDto(CodeProblem codeProblem) {
        this.id = codeProblem.getId();
        this.ownerName = codeProblem.getOwner().getName();
        this.title = codeProblem.getTitle();
        this.difficulty = codeProblem.getDifficulty().toString();
        this.tags = codeProblem.getTags();
        this.description = codeProblem.getDescription();
        this.solution = codeProblem.getSolution();
    }
}