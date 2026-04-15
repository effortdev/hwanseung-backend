package com.hwanseung.backend.domain.admin.dto;

import com.hwanseung.backend.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminUserResponseDto {

    private Long id;
    private String username;
    private String name;
    private String nickname;
    private String role;

    public static AdminUserResponseDto from(User user) {
        return AdminUserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .nickname(user.getNickname())
                .role(user.getRole().name()) // "ROLE_SUPER" 형태로 반환
                .build();
    }
}
