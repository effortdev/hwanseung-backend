package com.hwanseung.backend.domain.report.repository;

import com.hwanseung.backend.domain.admin.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByTypeAndReporterIdAndTargetProductId(
            String type,
            Long reporterId,
            Long targetProductId
    );

    List<Report> findByTargetProductId(Long targetProductId);
}