package com.hwanseung.backend.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportCheckResponseDTO {

    private boolean reported;
    private String message;

    public static ReportCheckResponseDTO of(boolean reported, String message) {
        return ReportCheckResponseDTO.builder()
                .reported(reported)
                .message(message)
                .build();
    }
}