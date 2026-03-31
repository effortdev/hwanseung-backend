package com.hwanseung.backend.domain.user.controller;

import com.hwanseung.backend.domain.user.dto.AuthRequestDTO;
import com.hwanseung.backend.domain.user.dto.AuthResponseDTO;
import com.hwanseung.backend.domain.user.dto.UserRequestDTO;
import com.hwanseung.backend.domain.user.service.AuthService;
import com.hwanseung.backend.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class AuthRestController {
    private final AuthService authService;
    private final UserService userService;

    /** 로그인 API */
    @PostMapping("/api/v1/auth/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO requestDto) {
        System.out.println("requestDto 로그인: "+requestDto);
        AuthResponseDTO responseDto = this.authService.login(requestDto);
        System.out.println("responseDto 로그인: "+responseDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    /** 회원가입 API */
    @PostMapping("/api/v1/auth/signup")
    public ResponseEntity<?> singUp(@RequestBody UserRequestDTO requestDto) {
        System.out.println("requestDto: "+requestDto);
        this.authService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }


    /** 토큰갱신 API */
    @GetMapping("/api/v1/auth/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("REFRESH_TOKEN") String refreshToken) {
        String newAccessToken = this.authService.refreshToken(refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(newAccessToken);
    }
}