package com.sky1sbloo.ocjsys.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private Long id;
    private String username;
    private List<String> authorities;
    private String jwtToken;
    private String refreshToken;
}
