package com.sky1sbloo.ocjsys.code.problem.verifier.dto;

import com.sky1sbloo.ocjsys.code.CodeLanguage;
import com.sky1sbloo.ocjsys.code.problem.dto.CodeProblemDto;
import com.sky1sbloo.ocjsys.code.problem.verifier.CodeProblemVerifier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CodeProblemVerifierDto {
    private Long id;
    private CodeProblemDto problem;
    CodeLanguage language;
    String sourceCode;

    public CodeProblemVerifierDto(CodeProblemVerifier verifier) {
        this.id = verifier.getId();
        this.problem = new CodeProblemDto(verifier.getProblem());
        this.language = verifier.getLanguage();
        this.sourceCode = verifier.getSourceCode();
    }
}
