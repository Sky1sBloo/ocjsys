package com.sky1sbloo.ocjsys.code.problem.initialcode;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeProblemInitialRepository extends JpaRepository<CodeProblemInitial, CodeProblemInitialId> {
}
