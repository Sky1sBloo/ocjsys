package com.sky1sbloo.ocjsys.code.problem.solutiontemplate;

import com.sky1sbloo.ocjsys.code.CodeLanguage;
import com.sky1sbloo.ocjsys.code.problem.CodeProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface SolutionTemplateRepository extends JpaRepository<SolutionTemplate, SolutionTemplateId> {
    Set<SolutionTemplate> findAllByCodeProblem_Id(Long codeProblem_id);
    SolutionTemplate findByCodeProblem_IdAndLanguage(Long codeProblem_id, CodeLanguage language);
}
