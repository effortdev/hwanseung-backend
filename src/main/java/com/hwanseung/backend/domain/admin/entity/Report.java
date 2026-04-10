package com.hwanseung.backend.domain.admin.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reports")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 신고 유형: USER, PRODUCT, CHAT */
    @Column(nullable = false, length = 20)
    private String type;

    /** 신고 처리 상태: PENDING, WARNED, SUSPENDED, DISMISSED, RESOLVED */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING";

    /** 신고 사유 카테고리: FRAUD, INAPPROPRIATE, ABUSIVE, SPAM, COUNTERFEIT, OTHER */
    @Column(length = 30)
    private String reasonCategory;

    /** 신고 상세 사유 */
    @Column(columnDefinition = "TEXT")
    private String reason;

    // --- 신고자 ---
    @Column(nullable = false)
    private Long reporterId;

    @Column(length = 50)
    private String reporterNickname;

    @Column(length = 100)
    private String reporterEmail;

    // --- 피신고자 ---
    @Column(nullable = false)
    private Long reportedUserId;

    @Column(length = 50)
    private String reportedNickname;

    @Column(length = 100)
    private String reportedEmail;

    /** 신고 대상 상품 ID (상품 신고인 경우) */
    private Long targetProductId;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ReportHistory> history = new ArrayList<>();
}
