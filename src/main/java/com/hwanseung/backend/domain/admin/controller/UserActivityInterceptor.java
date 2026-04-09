package com.hwanseung.backend.domain.admin.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class UserActivityInterceptor implements HandlerInterceptor {

    private final LoginManager loginManager;
    // 접속 유지 판단 시간: 5분 (300,000 밀리초) - 필요에 따라 조절하세요
    private static final long SESSION_TIMEOUT_MILLIS = 5 * 60 * 1000;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증된 사용자(익명 사용자 제외)인 경우 활동 시간 갱신
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            String userId = authentication.getName(); // JWT 등에서 추출된 유저 식별자
            long expirationTime = System.currentTimeMillis() + SESSION_TIMEOUT_MILLIS;
            loginManager.updateActivity(userId, expirationTime);
        }
        return true;
    }
}
