package com.hwanseung.backend.domain.user.config;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

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

        System.out.println("customUserDetails id: " + customUserDetails.getId());
        System.out.println("customUserDetails role: " + customUserDetails.getAuthorities());
        System.out.println("customUserDetails username: " + customUserDetails.getUsername());
        Date expiryDate = new Date(new Date().getTime() + jwtAccessTokenExpirationTime);
        String role = customUserDetails.getAuthorities().iterator().next().toString();
        return Jwts.builder()
                .setSubject(customUserDetails.getUsername())
                .claim("user-id", customUserDetails.getId())
                .claim("role", role)
                .claim("user-email", customUserDetails.getEmail())
                .setSubject(customUserDetails.getUsername()) // 글자 아이디 ("es")
                .claim("user-id", customUserDetails.getId()) // 고유 번호 (예: 1L)
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

    // 🌟 컨트롤러(DB 작업)에서 쓸 고유번호(Long) 꺼내기
    public Long getUserIdFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("user-id", Long.class);
    }

    // 🌟 시큐리티 필터에서 쓸 글자 아이디(String) 꺼내기
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
        try {
            Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            System.out.println("Invalid JWT token: " + ex.getMessage());
        }
        return false;
    }
}