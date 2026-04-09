package com.hwanseung.backend.domain.admin.dto;

/**
 * 카테고리별 거래 통계 DTO.
 * Product.category 를 기준으로 집계한다.
 */
public record TopCategoryDTO(String categoryName, long count, long amount) {}
