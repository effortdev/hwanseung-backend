package com.hwanseung.backend.domain.admin.repository;

import com.hwanseung.backend.domain.admin.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdminReportRepository extends JpaRepository<Report, Long> {

    /** 키워드 + 상태 + 유형 복합 검색 */
    @Query("SELECT r FROM Report r WHERE " +
           "(:keyword = '' OR r.reporterNickname LIKE %:keyword% OR r.reportedNickname LIKE %:keyword% OR r.reason LIKE %:keyword%) " +
           "AND (:status = '' OR r.status = :status) " +
           "AND (:type = '' OR r.type = :type) " +
           "ORDER BY r.createdAt DESC")
    Page<Report> searchReports(
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("type") String type,
            Pageable pageable);

    long countByStatus(String status);

    long countByReportedUserId(Long userId);
}
