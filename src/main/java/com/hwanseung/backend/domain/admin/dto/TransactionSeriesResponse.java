package com.hwanseung.backend.domain.admin.dto;

import java.util.List;

public record TransactionSeriesResponse(
        String period,
        List<TransactionSeriesPointDTO> series,
        TransactionSummaryDTO summary
) {}
