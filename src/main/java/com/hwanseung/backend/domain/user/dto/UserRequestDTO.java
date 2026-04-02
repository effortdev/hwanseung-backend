package com.hwanseung.backend.domain.user.dto;

import com.hwanseung.backend.domain.user.entity.Role;
import com.hwanseung.backend.domain.user.entity.User;
import lombok.*;

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
    private Role role;


    /**
     * DTO를 엔티티로 변환하는 메서드
     */
    public User toEntity() {
        return User.builder()
                .name(this.name)
                .username(this.username)
                .password(this.password)
                .nickname(this.nickname)
                .birthday(this.birthday)
                .contact(this.contact)
                .email(this.email)
                .gender(this.gender)
                .zipCode(this.zipCode)
                .address(this.address)
                .detailAddress(this.detailAddress)
                .role(this.role)         // 기본 권한 설정 (회원가입 시 보통 USER)
                .build();
    }
}