package com.sky1sbloo.ocjsys.code.problem.solutiontemplate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolutionTemplateRepository extends JpaRepository<SolutionTemplate, SolutionTemplateId> {
}
