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
@Builder
public class UserRequestDTO {
    private String username;
    private String name;
    private String password;
    private String nickname;
    private String birthday;
    private String contact;
    private String email;
    private String gender;
    private String zipCode;
    private String address;
    private String detailAddress;
    private Role role;
    private String neighborhood;
    private boolean isNeighborhoodAuthenticated;
    private String profileImagePath;
    private String profileOriginalName;
    private Status status;
    private Integer trustScore;
    private Integer reportCount;

    public User toEntity() {
        return User.builder()
                .name(this.name)
                .username(this.username)
                .password(this.password)
                .nickname(this.nickname)
                .email(hasText(this.email))
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

    private String hasText(String value) {
        return StringUtils.hasText(value) ? value : null;
    }
}