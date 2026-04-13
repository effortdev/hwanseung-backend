package com.hwanseung.backend.domain.user.dto;

import com.hwanseung.backend.domain.admin.dto.Status;
import com.hwanseung.backend.domain.user.entity.Role;
import com.hwanseung.backend.domain.user.entity.User;
import lombok.*;
import org.springframework.util.StringUtils;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder // 빌더 패턴 사용 가능하게 추가
public class UserRequestDTO {
    private String username;        // 로그인 아이디
    private String name;          // 실명/이름
    private String password;      // 비밀번호
    private String nickname;      // 별명
    private String birthday;      // 생년월일 (YYYY-MM-DD)
    private String contact;       // 연락처
    private String email;         // 이메일
    private String gender;        // 성별
    private String zipCode;       // 우편번호
    private String address;       // 주소
    private String detailAddress; // 상세 주소
    private Role role; //역할
    private String neighborhood; // 동네 이름 (예: 안양동)
    private boolean isNeighborhoodAuthenticated; // 인증 여부 (true/false)
    private String profileImagePath;
    private String profileOriginalName;
    private Status status;
    private Integer trustScore;
    private Integer reportCount;

    /**
     * DTO를 엔티티로 변환하는 메서드
     */
    public User toEntity() {
        return User.builder()
                .name(this.name)
                .username(this.username)
                .password(this.password)
                .nickname(this.nickname)
                .email(hasText(this.email))
                // 선택 입력 항목들: 빈 문자열이면 null로 처리
                .birthday(hasText(this.birthday))
                .contact(hasText(this.contact))
                .gender(hasText(this.gender))
                .zipCode(hasText(this.zipCode))
                .address(hasText(this.address))
                .detailAddress(hasText(this.detailAddress))
                .role(this.role)
                .profileImagePath(hasText(this.profileImagePath))
                .profileOriginalName(hasText(this.profileOriginalName))
                .status(this.status = (status != null) ? status : Status.ACTIVE)
                .trustScore(this.trustScore)
                .reportCount(this.reportCount)
                .build();
    }

    /**
     * 문자열이 비어있거나 공백만 있다면 null을 반환하고, 값이 있다면 그대로 반환합니다.
     */
    private String hasText(String value) {
        return StringUtils.hasText(value) ? value : null;
    }
}