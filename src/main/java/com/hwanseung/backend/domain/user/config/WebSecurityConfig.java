package com.hwanseung.backend.domain.user.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable()) // Spring Security와 같은 웹 보안 프레임워크에서 CSRF (Cross-Site Request Forgery) 보호 기능을 비활성화하는 설정. 세션을 사용하지 않고 토큰을 내보내야 하기 위해
                .cors(cors -> {}) // 추가: WebConfig의 CORS 설정을 Security에서도 사용하도록 연결
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 관리자 : 관리자 관련 모든 요청에 대해 승인된 사용자 중 ADMIN 권한이 있는 사용자만 허용
                        .requestMatchers("/api/v1/admin/**", "/api/v2/admin/**").hasRole("ADMIN")

                        // 회원가입 및 로그인 관련 모든 요청에 대해 아무나 승인
                        .requestMatchers("/api/v1/auth/**", "/api/v2/auth/**").permitAll()

                        // 상품페이지
                        .requestMatchers("/api/products/**").authenticated()

                        // 중복체크 관련 모든 요청에 대해 아무나 허용
                        .requestMatchers("/api/v1/user/check/**", "/api/v2/user/check/**").permitAll()

                        // 유저정보 관련 모든 요청에 대해 승인된 사용자만 허용
                        .requestMatchers("/api/v1/user/**", "/api/v2/user/**").authenticated()

                        // 첨부파일 관련 GET 요청에 대해 아무나 승인
                        .requestMatchers(HttpMethod.GET, "/api/v1/attachment/**", "/api/v2/attachment/**").permitAll()

                        // 댓글 관련 GET 요청에 대해 아무나 승인
                        .requestMatchers(HttpMethod.GET, "/api/v1/comment/**", "/api/v2/comment/**").permitAll()

                        // 게시글 관련 GET 요청에 대해 아무나 승인
                        .requestMatchers(HttpMethod.GET, "/api/v1/post/**", "/api/v2/post/**").permitAll()

                        // 기타 모든 요청에 대해 승인된 사용자만 허용
                        .requestMatchers("/api/v1/**", "/api/v2/**").authenticated()
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
