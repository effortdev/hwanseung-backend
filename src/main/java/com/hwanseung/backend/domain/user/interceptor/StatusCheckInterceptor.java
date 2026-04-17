package com.hwanseung.backend.domain.user.interceptor;

import com.hwanseung.backend.domain.user.config.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class StatusCheckInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {

            String statusFromToken = jwtTokenProvider.getStatusFromToken(token);

            if ("PENDING".equals(statusFromToken)) {
                String uri = request.getRequestURI();

                if (!uri.contains("/social-signup-extra") && !uri.contains("/api/auth/logout")) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json; charset=UTF-8");
                    response.getWriter().write("{\"code\": \"REQUIRED_EXTRA_INFO\", \"message\": \"추가 정보 입력이 필요합니다.\"}");
                    return false;
                }
            }
        }

        return true;
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}