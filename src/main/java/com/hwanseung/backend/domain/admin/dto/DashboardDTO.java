package com.hwanseung.backend.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class DashboardDTO {

    /** 주간 추이 차트 응답 */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeeklyTrendResponse {
        private List<String> labels;         // 날짜 라벨 (MM/dd 형식)
        private List<Long> transactions;     // 일별 거래 건수
        private List<Long> signups;          // 일별 가입 건수
    }

    /** 대시보드 요약 카드 응답 */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryResponse {
        private long activeTransactions;     // 진행 중 거래
        private long completedTransactions;  // 거래 완료
        private long pendingReports;         // 미처리 신고
    }

    /** 미처리 신고 항목 (대시보드용) */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PendingReportItem {
        private Long id;
        private String reasonCategory;     // FRAUD, SPAM 등
        private String reportedNickname;   // 피신고자 닉네임
        private String status;
        private String createdAt;          // 포맷된 문자열
    }

    /** 거래완료 로그 항목 (대시보드용) */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionLogItem {
        private Integer productId;
        private String title;
        private String sellerNickname;
        private int price;
        private String completedAt;        // 포맷된 문자열
    }

    /** 최근 등록 상품 항목 (대시보드용) */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentProductItem {
        private Integer productId;
        private String title;
        private int price;
        private String imageUrl;           // 첫 번째 이미지 URL (없으면 null)
        private String createdAt;          // 포맷된 문자열
    }
}
