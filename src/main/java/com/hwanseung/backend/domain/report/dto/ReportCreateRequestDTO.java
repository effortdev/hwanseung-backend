package com.hwanseung.backend.domain.report.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportCreateRequestDTO {

    private String reasonCategory; // FRAUD, SPAM, ABUSIVE, INAPPROPRIATE, COUNTERFEIT, PROHIBITED, OTHER
                                   // 사기/먹튀 , 광고/도배, 욕설/비방, 부적절한 내용, 가품, 거래 금지 물품, 기타
    private String reason;         // 상세 신고 사유
}