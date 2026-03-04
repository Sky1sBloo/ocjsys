package com.sky1sbloo.ocjsys.userprofile;

import com.sky1sbloo.ocjsys.auth.AuthUser;
import com.sky1sbloo.ocjsys.auth.AuthUserRepository;
import com.sky1sbloo.ocjsys.userprofile.dto.UserListDto;
import com.sky1sbloo.ocjsys.userprofile.dto.UserProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserProfileService {
    private final AuthUserRepository authUserRepository;

    @PreAuthorize("principal.username == #user.username")
    public UserProfileDto getUserProfile(AuthUser user) {
        return UserProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getUserProfile().getName())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName().name()).toList())
                .build();
    }

    @PreAuthorize("hasAuthority('READ_USERS_INFO')")
    public UserListDto getUserList() {
        List<AuthUser> authUser = authUserRepository.findAll();
        List<UserProfileDto> userInfoDto = new LinkedList<>();
        for (AuthUser user : authUser) {
            userInfoDto.add(
                    UserProfileDto.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .roles(user.getRoles().stream()
                                    .map(role -> role.getName().name()).toList()).build()
            );
        }
        return new UserListDto(userInfoDto);
    }
}
