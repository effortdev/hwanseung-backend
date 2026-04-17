package com.hwanseung.backend.domain.user.controller;

import com.hwanseung.backend.domain.user.config.CustomUserDetails;
import com.hwanseung.backend.domain.user.config.JwtTokenProvider;
import com.hwanseung.backend.domain.user.dto.TrustScoreHistoryDto;
import com.hwanseung.backend.domain.user.dto.UserRequestDTO;
import com.hwanseung.backend.domain.user.dto.UserResponseDTO;
import com.hwanseung.backend.domain.user.entity.TrustScoreHistory;
import com.hwanseung.backend.domain.user.entity.User;
import com.hwanseung.backend.domain.user.repository.UserRepository;
import com.hwanseung.backend.domain.user.service.TrustScoreService;
import com.hwanseung.backend.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TrustScoreService  trustScoreService;

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

    /** 회원탈퇴 API */
    @PostMapping("/api/user/withdraw")
    public ResponseEntity<?> deleteUser(@RequestBody Map<String, String> request,@RequestHeader("Authorization") String accessToken) {
        // 🌟 토큰에서 Long id를 꺼냅니다!
        try {
            String password = request.get("password");
            Long id = this.jwtTokenProvider.getUserIdFromToken(accessToken.substring(7));
            this.userService.withdraw(id, password);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }catch (RuntimeException e) {
            // 비밀번호 틀림 등의 사유를 400(Bad Request)으로 전달
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

    @PostMapping("/api/user/social-signup-extra")
    public ResponseEntity<?> updateSocialExtraInfo(
            @RequestBody UserRequestDTO userRequestDTO,
            Authentication authentication) {

        String username = authentication.getName();


        try {
                // 1. 유저 정보 업데이트
                User updatedUser = userService.completeSocialSignup(username, userRequestDTO.getContact());

                // 2. 새로운 토큰 생성
                String newAccessToken = jwtTokenProvider.generateAccessTokenFromUser(updatedUser);

                // 3. [수정된 부분] 유저 객체(또는 ID)를 전달하여 DB 토큰 갱신
                userService.updateAuthToken(updatedUser, newAccessToken);

                return ResponseEntity.ok().body(newAccessToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("저장 중 오류가 발생했습니다.");
        }
    }

    @GetMapping("/api/user/trust-score/history")
    public ResponseEntity<List<TrustScoreHistoryDto>> getTrustScoreHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails) { // 구현체 타입 확인 필요

        // 1. 서비스에서 엔티티 리스트 조회
        List<TrustScoreHistory> histories = trustScoreService.getHistory(userDetails.getId());

        // 2. 엔티티 리스트를 DTO 리스트로 변환 (매핑 로직)
        // .stream()을 이용해 각 엔티티를 DTO의 정적 메서드로 전달합니다.
        List<TrustScoreHistoryDto> dtoList = histories.stream()
                .map(TrustScoreHistoryDto::fromEntity)
                .collect(Collectors.toList());

        // 3. 결과 반환 (return 매핑 로직)
        // 데이터가 없을 경우 빈 리스트([])와 함께 200 OK가 나갑니다.
        return ResponseEntity.ok(dtoList);
    }

}