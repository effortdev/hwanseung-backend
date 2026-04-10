package com.hwanseung.backend.domain.report.repository;

import com.hwanseung.backend.domain.admin.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

    // 중복 신고 방지
    boolean existsByTypeAndReporterIdAndTargetProductId(
            String type,
            Long reporterId,
            Long targetProductId
    );
}