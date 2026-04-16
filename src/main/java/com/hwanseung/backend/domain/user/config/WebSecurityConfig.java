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
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(java.util.List.of("https://hsmarket.duckdns.org", "http://localhost:5173", "http://localhost"));
                    corsConfiguration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE","PATCH", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(java.util.List.of("*"));
                    corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                }))
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPER", "SUB")
                                .requestMatchers("/api/admin/manage").hasRole("SUPER")
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/api/user/social-signup-extra").authenticated()
                                .requestMatchers("/api/user/check/**").permitAll()
                                .requestMatchers("/api/search/popular").permitAll()
                                .requestMatchers("/api/search/log").permitAll()
                                .requestMatchers("/api/public/**").permitAll()

                                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/products/**").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/api/products/**").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/products/**").authenticated()
                                .requestMatchers("/api/notices/**").authenticated()
                                .requestMatchers("/api/inquiries/**").authenticated()

                                .requestMatchers("/api/imgs/**").permitAll()
                                .requestMatchers("/api/attachment/**").permitAll()

                                .requestMatchers("/ws-chat/**").permitAll()

                                .requestMatchers("/api/user/**").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/comment/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/post/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()

                        .requestMatchers("/api/**").authenticated()

                                .anyRequest().permitAll()
                )
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
