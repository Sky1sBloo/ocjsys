package com.sky1sbloo.ocjsys.code.problem;

import com.sky1sbloo.ocjsys.userprofile.UserProfile;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "code_problems")
public class CodeProblem {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner", referencedColumnName = "id", nullable = true)
    private UserProfile owner;
    private String title;
    @Enumerated(EnumType.STRING)
    private Difficulties difficulty;
    private List<String> tags;
    private String description;
    private String solution;
}
