package com.hwanseung.backend.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportCreateResponseDTO {

    private Long reportId;
    private String message;

    public static ReportCreateResponseDTO success(Long reportId) {
        return ReportCreateResponseDTO.builder()
                .reportId(reportId)
                .message("신고가 접수되었습니다.")
                .build();
    }
}