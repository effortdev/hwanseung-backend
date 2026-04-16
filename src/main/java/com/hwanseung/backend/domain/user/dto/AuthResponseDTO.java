package com.hwanseung.backend.domain.user.dto;

import com.hwanseung.backend.domain.user.entity.Auth;
import lombok.*;

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