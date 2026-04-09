package com.hwanseung.backend.domain.admin.dto;

import lombok.*;

import java.util.List;

public class StatisticsDTO {

    /** 1. 실시간 접속자 수 */
    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class OnlineUsersResponse {
        private int count;
    }

    /** 2. 사용자 통계 */
    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class UserStatsResponse {
        private long totalUsers;
        private long dailyNewUsers;
        private long monthlyNewUsers;
    }

    /** 3. 거래 통계 */
    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class TransactionStatsResponse {
        private long totalTransactions;
        private long totalGMV;
        private long dailyTransactions;
        private long monthlyTransactions;
    }

    /** 4. 상품 통계 */
    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class ProductStatsResponse {
        private long dailyListings;
        private long weeklyListings;
        private long monthlyListings;
        private long totalListings;
        private List<CategoryCount> categoryDistribution;
        private List<PriceRange> priceDistribution;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    public static class CategoryCount {
        private String name;
        private long count;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    public static class PriceRange {
        private String label;
        private long count;
    }

    /** 5. 검색 & 탐색 통계 */
    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class SearchStatsResponse {
        private List<KeywordCount> popularKeywords;
        private long totalWishlist;
        private long dailyWishlist;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    public static class KeywordCount {
        private String keyword;
        private long count;
    }

    /** 6. 신고 통계 */
    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class ReportStatsResponse {
        private long totalReports;
        private long pendingReports;
        private long resolvedReports;
        private long blockedUsers;
        private long suspendedUsers;
    }
}
