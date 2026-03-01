package com.sky1sbloo.ocjsys.auth;

import com.sky1sbloo.ocjsys.auth.dto.*;
import com.sky1sbloo.ocjsys.auth.jwt.JwtUtils;
import com.sky1sbloo.ocjsys.auth.refreshtoken.RefreshToken;
import com.sky1sbloo.ocjsys.auth.refreshtoken.RefreshTokenRepository;
import com.sky1sbloo.ocjsys.auth.refreshtoken.RefreshTokenService;
import com.sky1sbloo.ocjsys.auth.role.Role;
import com.sky1sbloo.ocjsys.auth.role.RoleRepository;
import com.sky1sbloo.ocjsys.auth.role.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RequiredArgsConstructor
@RestController
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final UserInfoRepository userInfoRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest userLoginDto) {
        Authentication auth;
        try {
            auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    userLoginDto.getUsername(), userLoginDto.getPassword()));
        } catch (AuthenticationException ex) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Wrong username/password");
            return new ResponseEntity<Object>(map, HttpStatus.UNAUTHORIZED);
        }
        SecurityContextHolder.getContext().setAuthentication(auth);
        UserInfo userDetails = (UserInfo) auth.getPrincipal();
        assert userDetails != null;
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();
        String jwtToken = jwtUtils.generateTokenFromUserDetails(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());
        var response = new LoginResponse(userDetails.getUsername(), roles, jwtToken, refreshToken.getToken());
        return ResponseEntity.ok(response);
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest userRegisterDto) {
        if (userInfoRepository.existsByUsername(userRegisterDto.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        UserInfo newUser = new UserInfo();
        newUser.setUsername(userRegisterDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));

        Optional<Role> defaultRole = roleRepository.findByName(Roles.USER);
        if (defaultRole.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cannot register default role");
        }
        newUser.setRoles(Set.of(defaultRole.get()));
        UserInfo user = userInfoRepository.save(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new RegisterResponse(user.getId(), user.getUsername())
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest refreshRequest) {
        if (refreshRequest.getRefreshToken() == null) {
            return ResponseEntity.badRequest().body(
                    "Missing refresh token"
            );
        }
        return refreshTokenRepository.findByToken(refreshRequest.getRefreshToken())
                .map(token -> {
                    if (refreshTokenService.isTokenExpired(token)) {
                        refreshTokenRepository.delete(token);
                        return ResponseEntity.badRequest().body("Refresh token expired");
                    }
                    String jwt = jwtUtils.generateTokenFromUserDetails(token.getUser());
                    var response = new RefreshResponse(jwt);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.badRequest().body("Invalid refresh token"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest logoutRequest) {
        if (logoutRequest.getRefreshToken() == null || logoutRequest.getRefreshToken().isBlank()) {
            return ResponseEntity.badRequest().body("Missing refresh token");
        }
        return refreshTokenRepository.findByToken(logoutRequest.getRefreshToken())
                .map(token -> {
                    refreshTokenRepository.delete(token);
                    return ResponseEntity.ok("Logged out successfully");
                })
                .orElse(
                        ResponseEntity.badRequest().body("Invalid refresh token")
                );
    }
}
