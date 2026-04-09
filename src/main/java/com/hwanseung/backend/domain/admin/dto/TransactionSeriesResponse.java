package com.hwanseung.backend.domain.admin.dto;

import java.util.List;

/** 거래 시계열 응답 (기간 + 포인트 목록 + 요약) */
public record TransactionSeriesResponse(
        String period,
        List<TransactionSeriesPointDTO> series,
        TransactionSummaryDTO summary
) {}
