package com.hwanseung.backend.domain.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    var config = new org.springframework.web.cors.CorsConfiguration();
                    config.setAllowedOriginPatterns(java.util.List.of("*")); // 모든 출처 허용 (테스트용)
                    config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                    config.setAllowedHeaders(java.util.List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                // 1. CSRF 보호 비활성화 (테스트용)
                .csrf(csrf -> csrf.disable())

                // 2. 기본 로그인 화면 안 뜨게 설정
                .formLogin(login -> login.disable())

                // 3. 웹소켓과 테스트 HTML은 누구나 접근 가능하도록 문을 활짝 열어둠
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/chat-test.html", "/ws-chat/**").permitAll()
                        .anyRequest().permitAll() // 지금은 채팅 테스트 단계라 모든 경로를 다 열어둡니다.
                );

        return http.build();
    }
}