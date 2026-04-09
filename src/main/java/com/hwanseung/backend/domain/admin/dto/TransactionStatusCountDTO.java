package com.hwanseung.backend.domain.admin.dto;

/**
 * 판매 상태별 집계 DTO.
 * status = Product.sale_status (예: SALE, SOLD_OUT)
 */
public record TransactionStatusCountDTO(String status, long count, long amount) {}
