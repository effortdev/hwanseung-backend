package com.hwanseung.backend.domain.user.dto;

import lombok.*;


//클라이언트측에서 서버측으로 API 요청을 보낼 때 사용될 DTO
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDTO {
    private String userid;
    private String password;
    private String role;
}
