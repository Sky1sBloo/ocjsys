package com.sky1sbloo.ocjsys.code.submission;

import com.sky1sbloo.ocjsys.code.problem.CodeProblem;
import com.sky1sbloo.ocjsys.userprofile.UserProfile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CodeSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="problem_id", nullable = false)
    private CodeProblem problem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="submitter_id", nullable = false)
    private UserProfile submitter;

    private String code;

    @Enumerated(EnumType.STRING)
    private CodeLanguage language;
}
