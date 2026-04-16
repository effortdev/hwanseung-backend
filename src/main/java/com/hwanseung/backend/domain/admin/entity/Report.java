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

    @Column(nullable = false, length = 20)
    private String type;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING";

    @Column(length = 30)
    private String reasonCategory;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(nullable = false)
    private Long reporterId;

    @Column(length = 50)
    private String reporterNickname;

    @Column(length = 100)
    private String reporterEmail;

    @Column(nullable = false)
    private Long reportedUserId;

    @Column(length = 50)
    private String reportedNickname;

    @Column(length = 100)
    private String reportedEmail;

    private Long targetProductId;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ReportHistory> history = new ArrayList<>();
}
