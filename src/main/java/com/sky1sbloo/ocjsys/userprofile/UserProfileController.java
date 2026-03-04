package com.sky1sbloo.ocjsys.userprofile;

import com.sky1sbloo.ocjsys.auth.AuthUser;
import com.sky1sbloo.ocjsys.userprofile.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
@RestController
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getUserInfo(@AuthenticationPrincipal AuthUser user) {
        return ResponseEntity.ok().body(userProfileService.getUserProfile(user));
    }

    @GetMapping("/")
    public ResponseEntity<UserListDto> getUsers() {
        return ResponseEntity.ok().body(userProfileService.getUserList());
    }
}
