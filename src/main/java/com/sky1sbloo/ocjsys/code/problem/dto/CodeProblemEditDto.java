package com.sky1sbloo.ocjsys.code.problem.dto;

import com.sky1sbloo.ocjsys.code.problem.Difficulties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeProblemEditDto {
    private long id;
    private String title;
    private Set<String> tags;
    private Difficulties difficulty;
    private String description;
    private String solution;
}
