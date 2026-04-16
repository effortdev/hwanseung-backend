package com.hwanseung.backend.domain.admin.dto;

import java.time.LocalDateTime;

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
