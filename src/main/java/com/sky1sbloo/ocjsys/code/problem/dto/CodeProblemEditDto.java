package com.sky1sbloo.ocjsys.code.problem.dto;

import com.sky1sbloo.ocjsys.code.problem.Difficulties;
import lombok.Builder;

import java.util.List;

@Builder
public record CodeProblemEditDto (
        long id,
        String title,
        List<String> tags,
        Difficulties difficulty,
        String description,
        String solution
){
}
