package com.sky1sbloo.ocjsys.code.problem;

import com.sky1sbloo.ocjsys.userprofile.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeProblemSearchFilter {
    private UserProfile owner;
    private String title;
    private List<String> tags;
    private List<Difficulties> difficulties;
}
