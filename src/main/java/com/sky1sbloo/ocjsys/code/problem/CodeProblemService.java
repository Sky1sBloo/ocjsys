package com.sky1sbloo.ocjsys.code.problem;

import com.sky1sbloo.ocjsys.code.problem.dto.CodeProblemSearchFilterDto;
import com.sky1sbloo.ocjsys.userprofile.UserProfile;
import com.sky1sbloo.ocjsys.userprofile.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CodeProblemService {
    private final CodeProblemRepository codeProblemRepository;
    private final UserProfileRepository userProfileRepository;

    public List<CodeProblem> findProblems(CodeProblemSearchFilter filter) {
        List<CodeProblem> codeProblems = new ArrayList<>();
        if (filter.getOwner() != null) {
            codeProblems.addAll(codeProblemRepository.findByOwner(filter.getOwner()));
        }
        if (filter.getTitle() != null) {
            codeProblems.addAll(codeProblemRepository.findByTitleIgnoreCaseContaining(filter.getTitle()));
        }
        if (filter.getDifficulties() != null && !filter.getDifficulties().isEmpty()) {
            for (Difficulties difficulty : filter.getDifficulties()) {
                codeProblems.addAll(codeProblemRepository.findByDifficulty(difficulty));
            }
        }
        if (filter.getTags() != null && !filter.getTags().isEmpty()) {
            for (String tag : filter.getTags()) {
                codeProblems.addAll(codeProblemRepository.findByTagsContaining(tag));
            }
        }
        return codeProblems;
    }

    public CodeProblemSearchFilter convertToFilter(CodeProblemSearchFilterDto filterDto) throws
            IllegalArgumentException {
        CodeProblemSearchFilter filter = new CodeProblemSearchFilter();
        if (filterDto.ownerName() != null) {
            UserProfile user = userProfileRepository.findByName(filterDto.ownerName()).orElse(null);
            filter.setOwner(user);
        }
        filter.setTitle(filterDto.title());
        filter.setTags(filterDto.tags());
        if (filterDto.difficulties() != null) {
            List<Difficulties> difficulties = new ArrayList<>();
            for (String difficultyStr : filterDto.difficulties()) {
                Difficulties difficulty = Difficulties.valueOf(difficultyStr.toUpperCase());
                difficulties.add(difficulty);
            }
            filter.setDifficulties(difficulties);
        }
        return filter;
    }
}
