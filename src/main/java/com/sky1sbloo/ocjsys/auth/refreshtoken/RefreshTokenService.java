package com.sky1sbloo.ocjsys.auth.refreshtoken;

import com.sky1sbloo.ocjsys.auth.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    @Value("${spring.auth.jwt.refresh-expiration-days}")
    private Long refreshExpirationDays;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserInfoRepository userInfoRepository;

    public RefreshToken createRefreshToken(String username) {
        var token = new RefreshToken();
        token.setUser(userInfoRepository.findByUsername(username).orElseThrow());
        token.setExpiryDate(Instant.now().plus(refreshExpirationDays, ChronoUnit.DAYS));
        token.setToken(UUID.randomUUID().toString());
        return refreshTokenRepository.save(token);
    }

    public boolean isTokenExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }
}
