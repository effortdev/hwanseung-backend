package com.hwanseung.backend.domain.inquiry.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryResponseDTO {

    private Long id;
    private String question;
    private String answer;
    private boolean answered;
}
