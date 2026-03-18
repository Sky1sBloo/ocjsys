package com.sky1sbloo.ocjsys.code.problem.solutiontemplate.dto;

import com.sky1sbloo.ocjsys.code.CodeLanguage;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SolutionTemplateGetDto {
    protected Long problemId;
    protected CodeLanguage language;
}
