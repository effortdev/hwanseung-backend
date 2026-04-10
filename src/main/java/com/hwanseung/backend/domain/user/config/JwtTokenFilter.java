package com.hwanseung.backend.domain.user.config;

// ... 기존 import 문들 ...
import com.hwanseung.backend.domain.admin.controller.LoginManager;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final LoginManager loginManager;

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, IOException {
//        String accessToken = getTokenFromRequest(request);
//        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
//            UsernamePasswordAuthenticationToken authentication = getAuthenticationFromToken(accessToken);
//            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//        filterChain.doFilter(request, response);
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = getTokenFromRequest(request);

        try {
            // 1. 토큰이 넘어왔을 때만 검사
            if (accessToken != null) {
                // 2. 토큰이 진짜인지 확인
                if (jwtTokenProvider.validateToken(accessToken)) {
                    UsernamePasswordAuthenticationToken authentication = getAuthenticationFromToken(accessToken);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // [핵심] 유저 아이디와 해당 토큰의 만료 시간을 LoginManager에 기록
                    // 토큰이 만료되면 자동으로 접속자 목록에서 제외됨
                    long exp = jwtTokenProvider.getUserIdFromToken(accessToken); // 토큰의 exp claim 추출
                    loginManager.updateActivity(authentication.getName(), exp);
                } else {
                    // 🚨 [핵심] 토큰은 넘어왔는데 가짜(변조/만료)인 경우!
                    // 여기서 바로 401 에러를 세팅하고 쫓아냅니다!
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json; charset=UTF-8");
                    response.getWriter().write("{\"error\": \"유효하지 않거나 변조된 토큰입니다.\"}");
                    return; // 🚨 return을 꼭 써야 컨트롤러로 안 넘어갑니다!
                }
            }
        } catch (Exception e) {
            // 🚨 만약 validateToken이나 getUserIdFromToken 내부에서 Exception이 터져나온 경우
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=UTF-8");
            response.getWriter().write("{\"error\": \"토큰 검증 중 오류가 발생했습니다.\"}");
            return; // 🚨 역시 여기서 쫓아냅니다!
        }

        // 3. 토큰이 아예 없는 비회원이거나, 정상적인 토큰인 경우에만 다음 필터로 통과!
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private UsernamePasswordAuthenticationToken getAuthenticationFromToken(String token) {
        // 🌟 시큐리티용 글자 아이디(예: "es")를 꺼내서 인증합니다.
        String username = jwtTokenProvider.getUsernameFromToken(token);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}