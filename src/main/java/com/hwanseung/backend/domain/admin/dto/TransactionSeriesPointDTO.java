package com.hwanseung.backend.domain.admin.dto;

/** 일/주/월 버킷 단위 거래 집계 포인트 */
public record TransactionSeriesPointDTO(String bucket, long count, long amount) {}
