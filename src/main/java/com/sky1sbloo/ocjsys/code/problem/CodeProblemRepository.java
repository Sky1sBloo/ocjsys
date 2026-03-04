package com.sky1sbloo.ocjsys.code.problem;

import com.sky1sbloo.ocjsys.userprofile.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeProblemRepository extends JpaRepository<CodeProblem, Long> {
    List<CodeProblem> findByOwner(UserProfile owner);
    List<CodeProblem> findByTitleIgnoreCaseContaining(String title);
    List<CodeProblem> findByDifficulty(Difficulties difficulty);
    List<CodeProblem> findByTagsContaining(String tag);
}
