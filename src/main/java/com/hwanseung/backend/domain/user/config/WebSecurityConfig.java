package com.hwanseung.backend.domain.user.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.hwanseung.backend.domain.user.config.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable()) // Spring Security와 같은 웹 보안 프레임워크에서 CSRF (Cross-Site Request Forgery) 보호 기능을 비활성화하는 설정. 세션을 사용하지 않고 토큰을 내보내야 하기 위해
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(java.util.List.of("https://hsmarket.duckdns.org", "http://localhost:5173", "http://localhost"));
                    corsConfiguration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE","PATCH", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(java.util.List.of("*"));
                    corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                })) // 추가: WebConfig의 CORS 설정을 Security에서도 사용하도록 연결
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
//                        // 관리자 : 관리자 관련 모든 요청에 대해 승인된 사용자 중 ADMIN 권한이 있는 사용자만 허용
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//
//                        // 관리자 : 카테고리 관련 모든 요청에 대해 승인된 사용자 중
//                        .requestMatchers("/api/admin/categories").hasAnyRole("ADMIN", "SUPER", "SUB")
//                        .requestMatchers("/api/stats/..").hasAnyRole("ADMIN", "SUPER", "SUB")
//
//                        // 회원가입 및 로그인 관련 모든 요청에 대해 아무나 승인
//                        .requestMatchers("/api/auth/**").permitAll()
//
//                        // 상품페이지
//                        .requestMatchers("/api/products/**").authenticated()
//
//                        // 채팅
//                        .requestMatchers("/ws-chat/**").authenticated()
//
//                        // 중복체크 관련 모든 요청에 대해 아무나 허용
//                        .requestMatchers("/api/user/check/**").permitAll()
//                        // 관리자 : 관리자 관련 모든 요청에 대해 승인된 사용자 중 ADMIN 권한이 있는 사용자만 허용
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//
//                        // 회원가입 및 로그인 관련 모든 요청에 대해 아무나 승인
//                        .requestMatchers("/api/auth/**").permitAll()
//
//                        // 상품페이지
//                        .requestMatchers("/api/products/**").authenticated()
//
//                        // 채팅
//                        .requestMatchers("/ws-chat/**").authenticated()
//
//                        // 중복체크 관련 모든 요청에 대해 아무나 허용
//                        .requestMatchers("/api/user/check/**").permitAll()
//
//                        // 유저정보 관련 모든 요청에 대해 승인된 사용자만 허용
//                        .requestMatchers("/api/user/**").authenticated()
//
//                        // 첨부파일 관련 GET 요청에 대해 아무나 승인
//                        .requestMatchers(HttpMethod.GET, "/api/attachment/**").permitAll()
//
//                        // 댓글 관련 GET 요청에 대해 아무나 승인
//                        .requestMatchers(HttpMethod.GET, "/api/comment/**").permitAll()
//
//                        // 게시글 관련 GET 요청에 대해 아무나 승인
//                        .requestMatchers(HttpMethod.GET, "/api/post/**").permitAll()
//
//                        // 기타 모든 요청에 대해 승인된 사용자만 허용
//                        .requestMatchers("/api/**").authenticated()
//
//                        // 그 외 나머지 모든 요청(정적 리소스 등)은 허용
//                        .anyRequest().permitAll()
                                // 1. [기존 유지] 관리자 및 인증 관련
                                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPER", "SUB")
                                .requestMatchers("/api/admin/manage").hasRole("SUPER")
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/api/user/social-signup-extra").authenticated()
                                .requestMatchers("/api/user/check/**").permitAll()
                                .requestMatchers("/api/search/popular").permitAll()
                                .requestMatchers("/api/search/log").permitAll()
                                .requestMatchers("/api/public/**").permitAll()

                                // 2. [수정/추가] 상품 페이지 관련
                                // 상품 상세 조회(GET)는 아무나 볼 수 있게 허용 (403 방지)
                                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                                // 상품 등록, 수정, 삭제는 로그인한 사용자만 가능
                                .requestMatchers(HttpMethod.POST, "/api/products/**").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/api/products/**").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/products/**").authenticated()
                                .requestMatchers("/api/notices/**").authenticated()
                                .requestMatchers("/api/inquiries/**").authenticated()

                                // 3. [추가] 이미지 및 정적 리소스 허용 (403 방지)
                                // 실제 이미지 경로인 /api/imgs/** 를 허용 목록에 추가
                                .requestMatchers("/api/imgs/**").permitAll()
                                .requestMatchers("/api/attachment/**").permitAll()

                                // 4. [추가] 채팅 웹소켓 허용
                                .requestMatchers("/ws-chat/**").permitAll()

                                // 5. [기존 유지] 유저, 댓글, 게시글 관련
                                .requestMatchers("/api/user/**").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/comment/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/post/**").permitAll()

                        // 카테고리 조회 일반 사용자 요청에 대해 승인
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()

                        // 기타 모든 요청에 대해 승인된 사용자만 허용
                        .requestMatchers("/api/**").authenticated()

                        // 그 외 나머지 모든 요청(정적 리소스 등)은 허용
                                .anyRequest().permitAll()
                )
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    //password를 암호화하는 기능으로 @Bean으로 등록하여 사용이 용이하도록 만듬.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
