package com.hwanseung.backend.domain.inquiry.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InquiryCreateRequestDTO {

    private String question;

    private Long userId;
}
