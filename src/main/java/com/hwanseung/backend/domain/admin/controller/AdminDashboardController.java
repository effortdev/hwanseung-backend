package com.hwanseung.backend.domain.admin.controller;

import com.hwanseung.backend.domain.admin.dto.DashboardDTO;
import com.hwanseung.backend.domain.admin.dto.DashboardDTO.*;
import com.hwanseung.backend.domain.admin.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    /** 주간 거래 및 가입 추이 (최근 7일) */
    @GetMapping("/weekly-trend")
    public ResponseEntity<WeeklyTrendResponse> getWeeklyTrend() {
        return ResponseEntity.ok(dashboardService.getWeeklyTrend());
    }

    /** 대시보드 요약 카드 (진행 중 거래, 거래 완료, 미처리 신고) */
    @GetMapping("/summary")
    public ResponseEntity<SummaryResponse> getDashboardSummary() {
        return ResponseEntity.ok(dashboardService.getDashboardSummary());
    }

    /** 미처리 신고 내역 (최근 7건) */
    @GetMapping("/pending-reports")
    public ResponseEntity<List<DashboardDTO.PendingReportItem>> getPendingReports() {
        return ResponseEntity.ok(dashboardService.getPendingReports());
    }

    /** 최근 거래완료 로그 (최근 10건) */
    @GetMapping("/transaction-logs")
    public ResponseEntity<List<DashboardDTO.TransactionLogItem>> getTransactionLogs() {
        return ResponseEntity.ok(dashboardService.getTransactionLogs());
    }

    /** 최근 등록 상품 (최근 6건) */
    @GetMapping("/recent-products")
    public ResponseEntity<List<DashboardDTO.RecentProductItem>> getRecentProducts() {
        return ResponseEntity.ok(dashboardService.getRecentProducts());
    }
}
