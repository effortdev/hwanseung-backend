package com.hwanseung.backend.domain.admin.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class AdminProductDTO {

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class ListResponse {
        private Long productId;
        private String title;
        private String category;
        private Integer price;
        private String location;
        private String sellerNickname;
        private String saleStatus;
        private String thumbnailUrl;
        private Integer reportCount;
        private LocalDateTime createdAt;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class DetailResponse {
        private Long productId;
        private String title;
        private String content;
        private String category;
        private Integer price;
        private String location;
        private String sellerId;
        private String sellerNickname;
        private String saleStatus;
        private String thumbnailUrl;
        private List<ProductImageDTO> productImages;
        private Integer reportCount;
        private String rejectReason;
        private String hideReason;
        private List<ProductReportDTO> reports;
        private LocalDateTime createdAt;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    public static class ProductImageDTO {
        private Long productImageId;
        private String imagePath;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    public static class ProductReportDTO {
        private String reason;
        private LocalDateTime createdAt;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ReasonRequest {
        private String reason;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class BulkRequest {
        private List<Integer> productIds;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class SummaryCount {
        private long total;
        private long sale;
        private long soldOut;
        private long reserved;
        private long hidden;
    }
}
