package com.hwanseung.backend.domain.user.dto;
import lombok.Data;

@Data
public class PayChargeVO {
    private String imp_uid;
    private String merchant_uid;
    private int amount;
}