package com.hwanseung.backend.domain.user.dto;

import lombok.Data;

@Data
public class PayUseVO {
    private String merchantUid;
    private int amount;
    private String username;
}