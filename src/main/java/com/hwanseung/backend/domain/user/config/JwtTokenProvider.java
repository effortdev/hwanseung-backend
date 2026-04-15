package com.hwanseung.backend.domain.user.config;

import com.hwanseung.backend.domain.user.entity.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

//JWT(JSON Web Token)의 생성, 분석, 유효성 검사 기능을 담당합니다.
@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String jwtSecretKey;
    @Value("${jwt.accessTokenExpirationTime}")
    private Long jwtAccessTokenExpirationTime;
    @Value("${jwt.refreshTokenExpirationTime}")
    private Long jwtRefreshTokenExpirationTime;

    public String generateAccessToken(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Date expiryDate = new Date(new Date().getTime() + jwtAccessTokenExpirationTime);
        return Jwts.builder()
                .setSubject(customUserDetails.getUsername())
                .claim("user-id", customUserDetails.getId())
                .claim("role", customUserDetails.getAuthorities().iterator().next().toString())
                .claim("user-email", customUserDetails.getEmail())
                .claim("status", customUserDetails.getStatus())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecretKey)
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Date expiryDate = new Date(new Date().getTime() + jwtRefreshTokenExpirationTime);
        return Jwts.builder()
                .setSubject(customUserDetails.getUsername())
                .claim("user-id", customUserDetails.getId())
                .claim("user-email", customUserDetails.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecretKey)
                .compact();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Long getUserIdFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("user-id", Long.class);
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getUserEmailFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("user-email", String.class);
    }

    public Date getExpirationFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecretKey)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Boolean validateToken(String token) {
        System.out.println("인증 토큰 유효성 체크");
        try {
            Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            System.out.println("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            System.out.println("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            System.out.println("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            System.out.println("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            System.out.println("JWT claims string is empty.");
        }
        return false;
    }

    public String generateAccessTokenFromUser(User user) {
        Date expiryDate = new Date(new Date().getTime() + jwtAccessTokenExpirationTime);

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("user-id", user.getId())
                .claim("role", user.getRole().name()) // 기존 권한 그대로 사용
                .claim("status", user.getStatus().name()) // 업데이트된 상태 사용
                .claim("user-email", user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecretKey)
                .compact();
    }

    public String getStatusFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("status", String.class);
    }
}