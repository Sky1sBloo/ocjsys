package com.sky1sbloo.ocjsys.code.testcase;

import com.sky1sbloo.ocjsys.code.CodeLanguage;
import com.sky1sbloo.ocjsys.code.problem.CodeProblem;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestCaseId {
    private CodeProblem problem;
    private CodeLanguage language;
}
