package com.sky1sbloo.ocjsys.code.problem;

import com.sky1sbloo.ocjsys.auth.AuthUser;
import com.sky1sbloo.ocjsys.code.problem.dto.CodeProblemCreateDto;
import com.sky1sbloo.ocjsys.code.problem.dto.CodeProblemEditDto;
import com.sky1sbloo.ocjsys.code.problem.dto.CodeProblemSearchFilterDto;
import com.sky1sbloo.ocjsys.userprofile.UserProfile;
import com.sky1sbloo.ocjsys.userprofile.UserProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class CodeProblemService {
    private final CodeProblemRepository codeProblemRepository;
    private final UserProfileRepository userProfileRepository;

    public Set<CodeProblem> findProblems(CodeProblemSearchFilter filter) {
        Set<CodeProblem> codeProblems = new HashSet<>();
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

    public CodeProblem findProblem(long id) {
        return codeProblemRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public CodeProblem createProblem(CodeProblemCreateDto codeProblemDto, AuthUser authUser)
            throws IllegalArgumentException {
        var newProblem = new CodeProblem();
        newProblem.setOwner(authUser.getUserProfile());
        newProblem.setTitle(codeProblemDto.getTitle());
        newProblem.setDescription(codeProblemDto.getDescription());
        newProblem.setSolution(codeProblemDto.getSolution());
        newProblem.setTags(codeProblemDto.getTags());
        newProblem.setDifficulty(codeProblemDto.getDifficulty());
        CodeProblem problem = codeProblemRepository.save(newProblem);

        return newProblem;
    }

    @Transactional
    public CodeProblem editProblem(CodeProblemEditDto codeProblemEditDto, AuthUser authUser)
            throws AccessDeniedException, IllegalArgumentException {
        CodeProblem codeProblem = codeProblemRepository.findById(codeProblemEditDto.getId()).orElseThrow(
                () -> new IllegalArgumentException("Problem with id " + codeProblemEditDto.getId() + " not found")
        );
        if (!codeProblem.getOwner().getAuthUser().getUsername().equals(authUser.getUsername())) {
            throw new AccessDeniedException("Forbidden");
        }
        Optional.ofNullable(codeProblem.getTitle()).ifPresent(codeProblem::setTitle);
        Optional.ofNullable(codeProblem.getDescription()).ifPresent(codeProblem::setDescription);
        Optional.ofNullable(codeProblem.getSolution()).ifPresent(codeProblem::setSolution);
        Optional.ofNullable(codeProblem.getTags()).ifPresent(codeProblem::setTags);
        Optional.ofNullable(codeProblem.getDifficulty()).ifPresent(codeProblem::setDifficulty);
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
            Set<Difficulties> difficulties = new HashSet<>();
            for (String difficultyStr : filterDto.difficulties()) {
                Difficulties difficulty = Difficulties.valueOf(difficultyStr.toUpperCase());
                difficulties.add(difficulty);
            }
            filter.setDifficulties(difficulties);
        }
        return filter;
    }
}
