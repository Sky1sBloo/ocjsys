package com.sky1sbloo.ocjsys.code.problem.dto;

import com.sky1sbloo.ocjsys.code.problem.CodeProblem;
import com.sky1sbloo.ocjsys.code.problem.Difficulties;
import com.sky1sbloo.ocjsys.code.problem.solutiontemplate.dto.SolutionTemplateDto;
import com.sky1sbloo.ocjsys.userprofile.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CodeProblemResponseDto {
    private Long id;
    private UserProfile owner;
    private String title;
    private Difficulties difficulty;
    private List<String> tags;
    private String description;
    private String solution;
    @Builder.Default
    private Set<SolutionTemplateDto> solutionTemplates = new HashSet<>();

    public CodeProblemResponseDto(CodeProblem codeProblem) {
        this.id = codeProblem.getId();
        this.owner = codeProblem.getOwner();
        this.title = codeProblem.getTitle();
        this.difficulty = codeProblem.getDifficulty();
        this.tags = codeProblem.getTags();
        this.description = codeProblem.getDescription();
        this.solution = codeProblem.getSolution();
        if (codeProblem.getSolutionTemplates() != null) {
            this.solutionTemplates = codeProblem.getSolutionTemplates().stream()
                    .map(SolutionTemplateDto::new)
                    .collect(Collectors.toSet());
        }
    }
}
