package com.sky1sbloo.ocjsys.code.testcase;

import com.sky1sbloo.ocjsys.code.CodeLanguage;
import com.sky1sbloo.ocjsys.code.problem.CodeProblem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@IdClass(TestCaseId.class)
public class TestCase {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem", referencedColumnName = "id", nullable = false)
    private CodeProblem problem;
    @Id
    CodeLanguage language;
    String sourceCode;
}
