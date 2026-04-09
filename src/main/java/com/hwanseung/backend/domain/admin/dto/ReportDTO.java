package com.hwanseung.backend.domain.admin.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class ReportDTO {

    /** 신고 목록 응답 */
    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class ListResponse {
        private Long id;
        private String type;
        private String status;
        private String reasonCategory;
        private String reason;
        private String reporterNickname;
        private String reportedNickname;
        private LocalDateTime createdAt;
    }

    /** 신고 상세 응답 */
    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class DetailResponse {
        private Long id;
        private String type;
        private String status;
        private String reasonCategory;
        private String reason;
        private String reporterNickname;
        private String reporterEmail;
        private String reportedNickname;
        private String reportedEmail;
        private Integer reportedReportCount;
        private TargetProductInfo targetProduct;
        private List<HistoryItem> history;
        private LocalDateTime createdAt;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    public static class TargetProductInfo {
        private Long productId;
        private String title;
        private Integer price;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    public static class HistoryItem {
        private String action;
        private String memo;
        private String adminNickname;
        private LocalDateTime createdAt;
    }

    /** 경고/기각 요청 */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class MemoRequest {
        private String memo;
    }

    /** 정지 요청 */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class SuspendRequest {
        private int days;
        private String memo;
    }

    /** 요약 카운트 */
    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class SummaryCount {
        private long total;
        private long pending;
        private long warned;
        private long suspended;
        private long dismissed;
    }

    /** 정지 사용자 응답 */
    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class SuspendedUserResponse {
        private Long id;
        private String nickname;
        private String email;
        private Integer trustScore;
        private Integer reportCount;
        private LocalDateTime suspendedAt;
        private LocalDateTime suspendUntil;
    }
}
