package com.hwanseung.backend.domain.user.dto;

import lombok.Data;

@Data
public class PayUseVO {
    private String merchantUid; // 물건 구매 고유번호 (주문번호)
    private int amount;         // 차감할 포인트(결제 금액)
}