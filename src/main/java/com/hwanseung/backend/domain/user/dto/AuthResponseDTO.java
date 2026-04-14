package com.hwanseung.backend.domain.user.dto;

import com.hwanseung.backend.domain.user.entity.Auth;
import lombok.*;

//인증 요청에 대해 User 정보와 함께 Token 정보를 응답으로 반환하는 클래스
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private String username;
    private String status;

    @Builder
    public AuthResponseDTO(Auth entity) {
        this.tokenType = entity.getTokenType();
        this.accessToken = entity.getAccessToken();
        this.refreshToken = entity.getRefreshToken();
        this.status = entity.getUser().getStatus().name();
    }
}