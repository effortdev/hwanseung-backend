package com.hwanseung.backend.domain.user.interceptor;

import com.hwanseung.backend.domain.user.config.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor // JwtTokenProvider 주입을 위해 필요합니다.
public class StatusCheckInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1. 요청 헤더에서 JWT 토큰 추출
        String token = resolveToken(request);

        // 2. 토큰이 존재하고 유효한지 검증 (서명 변조 체크)
        if (token != null && jwtTokenProvider.validateToken(token)) {

            // 3. 서버가 직접 토큰을 해독해서 '진짜 status'를 꺼냄 (F12 조작 방어)
            String statusFromToken = jwtTokenProvider.getStatusFromToken(token);

            // 4. 상태가 PENDING(추가정보 미입력)인 경우
            if ("PENDING".equals(statusFromToken)) {
                String uri = request.getRequestURI();

                // 추가 정보 입력 페이지나 로그아웃 요청이 아니라면 차단
                if (!uri.contains("/social-signup-extra") && !uri.contains("/api/auth/logout")) {
                    // 프론트엔드가 인지할 수 있도록 403 에러와 메시지 반환
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json; charset=UTF-8");
                    response.getWriter().write("{\"code\": \"REQUIRED_EXTRA_INFO\", \"message\": \"추가 정보 입력이 필요합니다.\"}");
                    return false; // 컨트롤러 진입 차단
                }
            }
        }

        // 토큰이 없거나(비회원), 정상이면 통과
        return true;
    }

    /**
     * HTTP 요청 헤더에서 Bearer 토큰을 추출하는 메서드
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}