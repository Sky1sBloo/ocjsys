package com.sky1sbloo.ocjsys.code.problem.initialcode;

import com.sky1sbloo.ocjsys.code.CodeLanguage;
import com.sky1sbloo.ocjsys.code.problem.CodeProblem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Used when loading a code problem, loads the initial source code for the user to edit
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@IdClass(CodeProblemInitialId.class)
public class CodeProblemInitial {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private CodeProblem codeProblem;
    @Id
    private CodeLanguage language;
    private String sourceCode;
}
