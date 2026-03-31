package com.hwanseung.backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Auth{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tokenType; //JWT 사용시 추후 Bearer 타입을 가지게 됨.

    @Column(nullable = false)
    private String accessToken; // 인증에 성공한 후 사용자에게 발급되는 단기 토큰으로 액세스 요청을 위한 수명이 짧은 Token

    @Column(nullable = false)
    private String refreshToken; //새 accessToken을 발급받기 위한 수명이 긴 Token. 액세스 토큰이 만료되면 사용자는 새로 고침 토큰을 사용하여 재인증 없이 새 액세스 토큰을 얻을 수 있다

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Auth(User user, String tokenType, String accessToken, String refreshToken) {
        this.user = user;
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}