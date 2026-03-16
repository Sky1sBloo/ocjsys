package com.sky1sbloo.ocjsys.code.problem.solver;

import com.sky1sbloo.ocjsys.code.problem.CodeProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeProblemSolverRepository extends JpaRepository<CodeProblemSolver, Long> {
    List<CodeProblemSolver> findByProblem(CodeProblem problem);
}
