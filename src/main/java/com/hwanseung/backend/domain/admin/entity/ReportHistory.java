package com.hwanseung.backend.domain.admin.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "report_history")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    /** 처리 행동: WARNED, SUSPENDED, DISMISSED */
    @Column(nullable = false, length = 20)
    private String action;

    /** 처리 메모 */
    @Column(columnDefinition = "TEXT")
    private String memo;

    /** 처리 관리자 닉네임 */
    @Column(length = 50)
    private String adminNickname;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
