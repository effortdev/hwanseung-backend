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
import org.springframework.security.core.Authentication;
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


    @GetMapping("/api/user")
    public ResponseEntity<?> findUser(@RequestHeader("Authorization") String accessToken) {
        Long id = this.jwtTokenProvider.getUserIdFromToken(accessToken.substring(7));
        UserResponseDTO userResponseDto = this.userService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
    }

    @PutMapping("/api/user")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String accessToken,
                                        @RequestPart("userData") UserRequestDTO requestDto,
                                        @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        try {
        Long id = this.jwtTokenProvider.getUserIdFromToken(accessToken.substring(7));
        this.userService.update(id, requestDto, profileImage);
        return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("이미지 업로드 실패");
        }

    }

    @PostMapping("/api/user/withdraw")
    public ResponseEntity<?> deleteUser(@RequestBody Map<String, String> request,@RequestHeader("Authorization") String accessToken) {
        try {
            String password = request.get("password");
            Long id = this.jwtTokenProvider.getUserIdFromToken(accessToken.substring(7));
            this.userService.withdraw(id, password);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

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
            @RequestBody Map<String, String> request) {

        String rawPassword = request.get("password");

        Long id = this.jwtTokenProvider.getUserIdFromToken(accessToken.substring(7));

        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다. id = " + id));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "비밀번호가 일치하지 않습니다."));
        }

        return ResponseEntity.ok(Map.of("message", "비밀번호가 확인되었습니다."));
    }

    @PostMapping("/api/user/social-signup-extra")
    public ResponseEntity<?> updateSocialExtraInfo(
            @RequestBody UserRequestDTO userRequestDTO,
            Authentication authentication) {

        String username = authentication.getName();


        try {
                User updatedUser = userService.completeSocialSignup(username, userRequestDTO.getContact());

                String newAccessToken = jwtTokenProvider.generateAccessTokenFromUser(updatedUser);

                userService.updateAuthToken(updatedUser, newAccessToken);

                return ResponseEntity.ok().body(newAccessToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("저장 중 오류가 발생했습니다.");
        }
    }


}