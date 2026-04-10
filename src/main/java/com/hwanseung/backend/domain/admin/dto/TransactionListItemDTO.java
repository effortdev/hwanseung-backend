package com.hwanseung.backend.domain.admin.dto;

import java.time.LocalDateTime;

/**
 * 관리자 거래 내역 목록 항목 DTO.
 * Product 엔티티 컬럼 기준:
 *   productId(상품PK), sellerId(판매자 ID, VARCHAR), sellerNickname(판매자 닉네임),
 *   title(상품명), category(카테고리), amount(price), status(sale_status), createdAt(생성일)
 */
public record TransactionListItemDTO(
        Integer productId,
        String sellerId,
        String sellerNickname,
        String title,
        String category,
        long amount,
        String status,
        LocalDateTime createdAt
) {}
