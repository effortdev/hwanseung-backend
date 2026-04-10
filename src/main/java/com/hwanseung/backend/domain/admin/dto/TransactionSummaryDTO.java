package com.hwanseung.backend.domain.admin.dto;

/** 거래 통계 요약 DTO */
public record TransactionSummaryDTO(long totalCount, long totalAmount, long avgAmount) {}
