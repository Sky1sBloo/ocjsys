package com.sky1sbloo.ocjsys.code.problem.verifier;

import com.sky1sbloo.ocjsys.code.problem.CodeProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeProblemVerifierRepository extends JpaRepository<CodeProblemVerifier, Long> {
    List<CodeProblemVerifier> findByProblem(CodeProblem problem);
}
