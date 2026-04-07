package com.hwanseung.backend.domain.user.controller;

import com.hwanseung.backend.domain.user.config.JwtTokenProvider;
import com.hwanseung.backend.domain.user.dto.UserRequestDTO;
import com.hwanseung.backend.domain.user.dto.UserResponseDTO;
import com.hwanseung.backend.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}