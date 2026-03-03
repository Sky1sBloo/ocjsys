package com.sky1sbloo.ocjsys.userprofile;

import com.sky1sbloo.ocjsys.auth.AuthUser;
import com.sky1sbloo.ocjsys.auth.UserInfoRepository;
import com.sky1sbloo.ocjsys.userprofile.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserProfileController {
    private final UserInfoRepository userInfoRepository;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getUserInfo(@AuthenticationPrincipal AuthUser user) {
        var userInfo = UserProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getUserProfile().getName())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().name()).collect(Collectors.toList())).build();
        return ResponseEntity.ok().body(userInfo);
    }

    @GetMapping("/")
    @PreAuthorize("hasAuthority('READ_USERS_INFO')")
    public ResponseEntity<UserListDto> getUsers() {
        List<AuthUser> authUser = userInfoRepository.findAll();
        List<UserProfileDto> userInfoDto = new LinkedList<>();
        for (AuthUser user : authUser) {
            userInfoDto.add(
                    UserProfileDto.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .roles(user.getRoles().stream()
                                    .map(role -> role.getName().name()).collect(Collectors.toList())).build()
            );
        }
        var userListDto = new UserListDto(userInfoDto);
        return ResponseEntity.ok().body(userListDto);
    }
}
