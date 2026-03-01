package com.sky1sbloo.ocjsys.auth.jwt;

import com.sky1sbloo.ocjsys.auth.UserInfo;
import com.sky1sbloo.ocjsys.auth.role.Permission;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtils {
    @Value("${spring.auth.jwt.secret}")
    private String jwtSecret;
    @Value("${spring.auth.jwt.expiration-mins}")
    private int jwtExpirationMins;
    public String getJwtFromHeader(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        log.debug("Authorization header: {}", token);
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    public String generateTokenFromUserDetails(UserInfo userDetails) {
        List<String> roles = userDetails.getRoles().stream()
                .map(role -> role.getName().name()).toList();
        Set<String> permissions = userDetails.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());
        return Jwts.builder()
                .claim("roles", roles)
                .claim("permissions", permissions)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(jwtExpirationMins, ChronoUnit.MINUTES)))
                .signWith(key())
                .compact();
    }

    public String getUsernameFromWebToken(String token) {
        return Jwts.parser().verifyWith((SecretKey) key()).build()
                .parseSignedClaims(token)
                .getPayload().getSubject();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Malformed jwt token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired jwt token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported jwt token : {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("Jwt claims string empty: {}", ex.getMessage());
        }
        return false;
    }
}
