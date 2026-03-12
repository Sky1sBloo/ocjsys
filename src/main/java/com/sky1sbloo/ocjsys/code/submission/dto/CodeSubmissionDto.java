package com.sky1sbloo.ocjsys.code.submission.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CodeSubmissionDto {
    private Long problemId;
    private String sourceCode;
    private String language;
}
