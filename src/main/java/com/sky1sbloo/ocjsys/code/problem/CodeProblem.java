package com.sky1sbloo.ocjsys.code.problem;

import com.sky1sbloo.ocjsys.code.problem.solutiontemplate.SolutionTemplate;
import com.sky1sbloo.ocjsys.userprofile.UserProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "code_problems")
public class CodeProblem {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner", referencedColumnName = "id")
    private UserProfile owner;
    private String title;
    @Enumerated(EnumType.STRING)
    private Difficulties difficulty;
    @ElementCollection
    @CollectionTable(name="code_problem_tags", joinColumns = @JoinColumn(name="code_problem_id",
            referencedColumnName = "id"))
    @Column(name="tag")
    private Set<String> tags;
    private String description;
    private String solution;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "codeProblem", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SolutionTemplate> solutionTemplates = new HashSet<>();
}
