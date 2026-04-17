package com.hwanseung.backend.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hwanseung.backend.domain.user.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDTO {
    private Long id;
    private String name;
    private String username;
    private String nickname;
    private String birthday;
    private String contact;
    private String email;
    private String gender;
    private String zipCode;
    private String address;
    private String detailAddress;
    private String role;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private String profileImagePath;
    private String profileOriginalName;
    private String status;
    private String provider;

    private Integer trustScore;
    private Integer level;
    private Integer nextLevelRemaining; // 👈 추가: 다음 레벨까지 남은 점수
    private String levelName;            // 👈 추가: "새싹", "신뢰왕" 등 명칭 (선택사항)

    // 로직 검증 후 추가된 생성자/빌더 내 계산 메서드
    public void calculateLevelInfo() {
        // 1. null 체크 및 0점 초기화
        if (this.trustScore == null) this.trustScore = 0;

        // 2. 0점일 때의 명확한 처리 (이 블록이 없으면 아래 < 20 조건에 걸려 1레벨이 됨)
        if (this.trustScore == 0) {
            this.level = 0;
            this.nextLevelRemaining = 20; // 다음 레벨(1레벨)까지 필요한 점수
            return; // 👈 0점인 경우 여기서 로직 종료
        }

        // 3. 점수 구간별 레벨 설정 (프론트엔드 기준과 일치시킴)
        if (this.trustScore < 20) {
            this.level = 1;
            this.nextLevelRemaining = 20 - this.trustScore;
        } else if (this.trustScore < 100) {
            this.level = 2;
            this.nextLevelRemaining = 100 - this.trustScore;
        } else if (this.trustScore < 400) {
            this.level = 3;
            this.nextLevelRemaining = 400 - this.trustScore;
        } else if (this.trustScore < 900) {
            this.level = 4;
            this.nextLevelRemaining = 900 - this.trustScore;
        } else if (this.trustScore < 1500) {
            this.level = 5;
            this.nextLevelRemaining = 1500 - this.trustScore;
        } else {
            this.level = 6;
            this.nextLevelRemaining = 0;
        }
    }

    public UserResponseDTO(User entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.username = entity.getUsername();
        this.nickname = entity.getNickname();
        this.birthday = entity.getBirthday();
        this.contact = entity.getContact();
        this.email = entity.getEmail();
        this.gender = entity.getGender();
        this.zipCode = entity.getZipCode();
        this.address = entity.getAddress();
        this.detailAddress = entity.getDetailAddress();
        this.role = entity.getRole() != null ? entity.getRole().name() : null;
        this.createdAt = entity.getCreatedAt();
        this.profileImagePath = entity.getProfileImagePath();
        this.profileOriginalName = entity.getProfileOriginalName();
        this.status = entity.getStatus().name();
        this.provider = entity.getProvider();

        // 1. 엔티티에서 신뢰 점수를 가져와서 DTO 필드에 먼저 채웁니다. (누락되었던 부분)
        this.trustScore = entity.getTrustScore();

        // 2. 점수가 채워진 상태에서 계산 로직을 호출합니다. (누락되었던 부분)
        this.calculateLevelInfo();
    }
}