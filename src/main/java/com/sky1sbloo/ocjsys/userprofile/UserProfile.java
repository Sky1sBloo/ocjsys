package com.sky1sbloo.ocjsys.userprofile;

import com.sky1sbloo.ocjsys.auth.AuthUser;
import com.sky1sbloo.ocjsys.code.problem.CodeProblem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    private Long id;
    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name="id")
    private AuthUser authUser;

    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private Set<CodeProblem> codeProblems = new HashSet<>();
}
