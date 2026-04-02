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

import java.util.Map;

@RequiredArgsConstructor
@RestController
public class AuthRestController {
    private final AuthService authService;
    private final UserService userService;

    /** 로그인 API */
    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO requestDto) {
        System.out.println("requestDto 로그인: "+requestDto);
        AuthResponseDTO responseDto = this.authService.login(requestDto);
        System.out.println("responseDto 로그인: "+responseDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    /** 회원가입 API */
    @PostMapping("/api/auth/signup")
    public ResponseEntity<?> singUp(@RequestBody UserRequestDTO requestDto) {
        System.out.println("requestDto: "+requestDto);
        this.authService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }


    /** 토큰갱신 API */
    @GetMapping("/api/auth/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("REFRESH_TOKEN") String refreshToken) {
        String newAccessToken = this.authService.refreshToken(refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(newAccessToken);
    }

    // 아이디 중복 체크
    @GetMapping("/api/auth/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUserid(@RequestParam("username") String username) {
        boolean isDuplicate = userService.isUseridDuplicate(username);
        return ResponseEntity.ok(Map.of("isDuplicate", isDuplicate));
    }

    // 닉네임 중복 체크
    @GetMapping("/api/auth/check-nickname")
    public ResponseEntity<Map<String, Boolean>> checkNickname(@RequestParam("nickname") String nickname) {
        boolean isDuplicate = userService.isNicknameDuplicate(nickname);
        return ResponseEntity.ok(Map.of("isDuplicate", isDuplicate));
    }

    // 이메일 중복 체크
    @GetMapping("/api/auth/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam("email") String email) {
        boolean isDuplicate = userService.isEmailDuplicate(email);
        return ResponseEntity.ok(Map.of("isDuplicate", isDuplicate));
    }


}