package com.hwanseung.backend.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class DashboardDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeeklyTrendResponse {
        private List<String> labels;
        private List<Long> transactions;
        private List<Long> signups;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryResponse {
        private long activeTransactions;
        private long completedTransactions;
        private long pendingReports;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PendingReportItem {
        private Long id;
        private String reasonCategory;
        private String reportedNickname;
        private String status;
        private String createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionLogItem {
        private Integer productId;
        private String title;
        private String sellerNickname;
        private int price;
        private String completedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentProductItem {
        private Integer productId;
        private String title;
        private int price;
        private String imageUrl;
        private String createdAt;
    }
}
