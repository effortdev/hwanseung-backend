package com.hwanseung.backend.domain.user.dto;
import lombok.Data;

@Data
public class AccountVerifyRequest {
    private String bankCode;
    private String accountNumber;
}