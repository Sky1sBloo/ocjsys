package com.sky1sbloo.ocjsys.code.problem;

import com.sky1sbloo.ocjsys.auth.AuthUser;
import com.sky1sbloo.ocjsys.code.CodeLanguage;
import com.sky1sbloo.ocjsys.code.problem.dto.CodeProblemCreateDto;
import com.sky1sbloo.ocjsys.code.problem.dto.CodeProblemEditDto;
import com.sky1sbloo.ocjsys.code.problem.dto.CodeProblemSearchFilterDto;
import com.sky1sbloo.ocjsys.code.problem.verifier.CodeProblemVerifier;
import com.sky1sbloo.ocjsys.code.problem.verifier.CodeProblemVerifierRepository;
import com.sky1sbloo.ocjsys.code.problem.verifier.dto.CodeProblemVerifierDto;
import com.sky1sbloo.ocjsys.userprofile.UserProfile;
import com.sky1sbloo.ocjsys.userprofile.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CodeProblemService {
    private final CodeProblemRepository codeProblemRepository;
    private final UserProfileRepository userProfileRepository;
    private final CodeProblemVerifierRepository codeProblemVerifierRepository;

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

    @Transactional
    public CodeProblem createProblem(CodeProblemCreateDto codeProblem, AuthUser authUser)
            throws IllegalArgumentException {
        var newProblem = new CodeProblem();
        newProblem.setOwner(authUser.getUserProfile());
        newProblem.setTitle(codeProblem.title());
        newProblem.setDescription(codeProblem.description());
        newProblem.setSolution(codeProblem.solution());
        newProblem.setTags(codeProblem.tags());
        newProblem.setDifficulty(Difficulties.valueOf(codeProblem.difficulty().toUpperCase()));
        CodeProblem problem = codeProblemRepository.save(newProblem);
        for (CodeProblemVerifierDto verifierDto : codeProblem.verifiers()) {
            CodeProblemVerifier verifier = CodeProblemVerifier.builder()
                    .problem(problem)
                    .language(CodeLanguage.valueOf(verifierDto.language().toUpperCase())).build();
            codeProblemVerifierRepository.save(verifier);
        }
        return newProblem;
    }

    @Transactional
    public CodeProblem editProblem(CodeProblemEditDto codeProblemEditDto, AuthUser authUser)
            throws AccessDeniedException, IllegalArgumentException {
        CodeProblem codeProblem = codeProblemRepository.findById(codeProblemEditDto.id()).orElseThrow(
                () -> new IllegalArgumentException("Problem with id " + codeProblemEditDto.id() + " not found")
        );
        if (!codeProblem.getOwner().getAuthUser().getUsername().equals(authUser.getUsername())) {
            throw new AccessDeniedException("Forbidden");
        }
        if (codeProblemEditDto.title() != null) {
            codeProblem.setTitle(codeProblemEditDto.title());
        }
        if (codeProblemEditDto.description() != null) {
            codeProblem.setDescription(codeProblemEditDto.description());
        }
        if (codeProblemEditDto.solution() != null) {
            codeProblem.setSolution(codeProblemEditDto.solution());
        }
        if (codeProblemEditDto.tags() != null) {
            codeProblem.setTags(codeProblemEditDto.tags());
        }
        if (codeProblemEditDto.difficulty() != null) {
            codeProblem.setDifficulty(Difficulties.valueOf(codeProblemEditDto.difficulty().toUpperCase()));
        }
        return codeProblemRepository.save(codeProblem);
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
