package com.hwanseung.backend.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PayChargeVO {
    @JsonProperty("imp_uid")
    private String impUid; // 🌟 자바 표준 이름으로 변경!

    @JsonProperty("merchant_uid")
    private String merchantUid; // 🌟 자바 표준 이름으로 변경!

    private int amount;
}