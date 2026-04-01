package com.hwanseung.backend.domain.user.dto;
import lombok.Data;

@Data
public class AccountVerifyRequest {
    private String bankCode;      // 리액트에서 보낼 은행 코드
    private String accountNumber; // 리액트에서 보낼 계좌번호
}