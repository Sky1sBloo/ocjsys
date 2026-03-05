package com.sky1sbloo.ocjsys.auth;

import com.sky1sbloo.ocjsys.auth.dto.*;
import com.sky1sbloo.ocjsys.auth.jwt.JwtUtils;
import com.sky1sbloo.ocjsys.auth.refreshtoken.RefreshToken;
import com.sky1sbloo.ocjsys.auth.refreshtoken.RefreshTokenRepository;
import com.sky1sbloo.ocjsys.auth.refreshtoken.RefreshTokenService;
import com.sky1sbloo.ocjsys.auth.role.Role;
import com.sky1sbloo.ocjsys.auth.role.RoleRepository;
import com.sky1sbloo.ocjsys.auth.role.Roles;
import com.sky1sbloo.ocjsys.userprofile.UserProfile;
import com.sky1sbloo.ocjsys.userprofile.UserProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthUserRepository authUserRepository;
    private final UserProfileRepository userProfileRepository;
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
        AuthUser userDetails = (AuthUser) auth.getPrincipal();
        assert userDetails != null;
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();
        String jwtToken = jwtUtils.generateTokenFromUserDetails(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());
        var response = new LoginResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getUserProfile().getName(),
                roles,
                jwtToken,
                refreshToken.getToken());
        return ResponseEntity.ok(response);
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest userRegisterDto) {
        if (authUserRepository.existsByUsername(userRegisterDto.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        AuthUser newUser = new AuthUser();
        newUser.setUsername(userRegisterDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));

        Optional<Role> defaultRole = roleRepository.findByName(Roles.USER);
        if (defaultRole.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cannot register default role");
        }
        newUser.setRoles(Set.of(defaultRole.get()));
        AuthUser user = authUserRepository.save(newUser);
        UserProfile newUserProfile = UserProfile.builder()
                .name(userRegisterDto.getName())
                .authUser(user).build();
        userProfileRepository.save(newUserProfile);
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

    @PutMapping("/role")
    @PreAuthorize("hasAuthority('CHANGE_USER_ROLE')")
    public ResponseEntity<?> setUserRole(@RequestParam(value = "id") long userId,
                                         @RequestParam(value = "roles") List<String> roleNames) {
        Set<Roles> rolesEnum = new HashSet<>();
        try {
            for (String roleName : roleNames) {
                rolesEnum.add(Roles.valueOf(roleName));
            }

            Set<Role> roles = new HashSet<>();
            for (Roles roleEnum : rolesEnum) {
                Role role = roleRepository.findByName(roleEnum)
                        .orElseThrow(() -> new EntityNotFoundException("Cannot find role: " + roleEnum.name()));
                roles.add(role);
            }
            Optional<AuthUser> userInfo = authUserRepository.findById(userId);
            if (userInfo.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot find user");
            }
            userInfo.get().setRoles(roles);
            authUserRepository.save(userInfo.get());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid role name");
        } catch (EntityNotFoundException ex) {
            log.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found");
        }
    }
}
