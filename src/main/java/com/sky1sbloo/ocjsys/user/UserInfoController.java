package com.sky1sbloo.ocjsys.user;

import com.sky1sbloo.ocjsys.auth.UserInfo;
import com.sky1sbloo.ocjsys.auth.UserInfoRepository;
import com.sky1sbloo.ocjsys.auth.role.Role;
import com.sky1sbloo.ocjsys.auth.role.RoleRepository;
import com.sky1sbloo.ocjsys.auth.role.Roles;
import com.sky1sbloo.ocjsys.user.dto.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserInfoController {
    private final UserInfoRepository userInfoRepository;
    private final RoleRepository roleRepository;

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
            Optional<UserInfo> userInfo = userInfoRepository.findById(userId);
            if (userInfo.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot find user");
            }
            userInfo.get().setRoles(roles);
            userInfoRepository.save(userInfo.get());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid role name");
        } catch (EntityNotFoundException ex) {
            log.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Role not found");
        }
    }
}
