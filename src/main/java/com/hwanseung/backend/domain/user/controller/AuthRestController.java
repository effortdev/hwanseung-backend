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
@CrossOrigin(origins = "http://localhost:5173")
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

    /** 1. 이메일 인증번호 발송 */
    @PostMapping("/api/auth/email/send-code")
    public ResponseEntity<?> sendEmailCode(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            authService.requestEmailVerification(email);
            return ResponseEntity.ok("인증번호가 이메일로 발송되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메일 발송 중 오류가 발생했습니다.");
        }
    }

    /** 2. SMS 인증번호 발송 */
    @PostMapping("/api/auth/sms/send-code")
    public ResponseEntity<?> sendSmsCode(@RequestBody Map<String, String> request) {
        try {
            String phoneNumber = request.get("phoneNumber");
            authService.requestSmsVerification(phoneNumber);
            return ResponseEntity.ok("인증번호가 문자로 발송되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SMS 발송 중 오류가 발생했습니다.");
        }
    }

    /** 3. 인증번호 검증 (공통) */
    @PostMapping("/api/auth/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> request) {
        String key = request.get("key");   // email 또는 phoneNumber
        String code = request.get("code"); // 사용자가 입력한 6자리 번호

        boolean isVerified = authService.checkVerification(key, code);

        if (isVerified) {
            return ResponseEntity.ok("인증에 성공하였습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증번호가 일치하지 않거나 만료되었습니다.");
        }
    }

}