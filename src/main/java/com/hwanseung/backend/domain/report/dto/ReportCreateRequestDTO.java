package com.hwanseung.backend.domain.report.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportCreateRequestDTO {

    private String reasonCategory;
    private String reason;
}