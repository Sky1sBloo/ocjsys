package com.sky1sbloo.ocjsys.code.problem.solutiontemplate.dto;

import com.sky1sbloo.ocjsys.code.CodeLanguage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Used by the code problem service
 * @see SolutionTemplateDto for adding or deletion of with problems already initialized
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SolutionTemplateCreateDto {
    private CodeLanguage language;
    private String sourceCode;  // shows when loading the problem in the specified language
    private String verifierSourceCode;  // used to test the source code
}

