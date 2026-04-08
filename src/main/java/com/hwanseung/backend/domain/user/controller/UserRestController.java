package com.hwanseung.backend.domain.user.controller;

import com.hwanseung.backend.domain.user.config.JwtTokenProvider;
import com.hwanseung.backend.domain.user.dto.UserRequestDTO;
import com.hwanseung.backend.domain.user.dto.UserResponseDTO;
import com.hwanseung.backend.domain.user.entity.User;
import com.hwanseung.backend.domain.user.repository.UserRepository;
import com.hwanseung.backend.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /** 회원정보 조회 API */
    @GetMapping("/api/user")
    public ResponseEntity<?> findUser(@RequestHeader("Authorization") String accessToken) {
        // 🌟 토큰에서 Long id를 꺼냅니다!
        Long id = this.jwtTokenProvider.getUserIdFromToken(accessToken.substring(7));
        UserResponseDTO userResponseDto = this.userService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
    }

    /** 회원정보 수정 API */
    @PutMapping("/api/user")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String accessToken,
                                        @RequestPart("userData") UserRequestDTO requestDto,
                                        @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        try {
        // 🌟 토큰에서 Long id를 꺼냅니다!
        Long id = this.jwtTokenProvider.getUserIdFromToken(accessToken.substring(7));
        this.userService.update(id, requestDto, profileImage);
        return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("이미지 업로드 실패");
        }

    }

    /** 회원정보 삭제 API */
    @DeleteMapping("/api/user")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String accessToken) {
        // 🌟 토큰에서 Long id를 꺼냅니다!
        Long id = this.jwtTokenProvider.getUserIdFromToken(accessToken.substring(7));
        this.userService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }


    @GetMapping("/api/user/count")
    public ResponseEntity<Map<String, Long>> getUserCount() {
        long count = userService.getTotalUserCount();
        Map<String, Long> response = new HashMap<>();
        response.put("totalCount", count);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/user/verify-password")
    public ResponseEntity<?> verifyPassword(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody Map<String, String> request) { // DTO를 따로 안 만들고 Map으로 "password"를 받습니다.

        // 1. 프론트엔드 팝업창에서 사용자가 방금 입력한 날것의 비밀번호 (예: "1234")
        String rawPassword = request.get("password");

        // 2. 학생분의 기존 방식과 완벽하게 동일하게! 토큰에서 고유 id를 뽑아냅니다.
        Long id = this.jwtTokenProvider.getUserIdFromToken(accessToken.substring(7));

        // 3. id를 이용해 DB에서 진짜 유저 정보를 꺼내옵니다.
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다. id = " + id));

        // 4. 🌟 방금 배운 핵심 로직! 감식기(matches)를 돌립니다.
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {

            // ❌ 틀리면 401(Unauthorized) 에러를 프론트엔드로 던집니다.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "비밀번호가 일치하지 않습니다."));
        }

        // ✅ 맞으면 200(OK) 성공 신호를 보냅니다.
        return ResponseEntity.ok(Map.of("message", "비밀번호가 확인되었습니다."));
    }


}