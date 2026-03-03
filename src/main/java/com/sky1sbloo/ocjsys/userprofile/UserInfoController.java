package com.sky1sbloo.ocjsys.userprofile;

import com.sky1sbloo.ocjsys.auth.UserInfo;
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
public class UserInfoController {
    private final UserInfoRepository userInfoRepository;

    @GetMapping("/profile")
    public ResponseEntity<UserInfoDto> getUserInfo(@AuthenticationPrincipal UserInfo user) {
        var userInfo = UserInfoDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().name()).collect(Collectors.toList())).build();
        return ResponseEntity.ok().body(userInfo);
    }

    @GetMapping("/")
    @PreAuthorize("hasAuthority('READ_USERS_INFO')")
    public ResponseEntity<UserListDto> getUsers() {
        List<UserInfo> userInfo = userInfoRepository.findAll();
        List<UserInfoDto> userInfoDto = new LinkedList<>();
        for (UserInfo user : userInfo) {
            userInfoDto.add(
                    UserInfoDto.builder()
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
