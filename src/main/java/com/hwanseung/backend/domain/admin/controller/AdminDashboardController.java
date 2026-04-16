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

    @GetMapping("/weekly-trend")
    public ResponseEntity<WeeklyTrendResponse> getWeeklyTrend() {
        return ResponseEntity.ok(dashboardService.getWeeklyTrend());
    }

    @GetMapping("/summary")
    public ResponseEntity<SummaryResponse> getDashboardSummary() {
        return ResponseEntity.ok(dashboardService.getDashboardSummary());
    }

    @GetMapping("/pending-reports")
    public ResponseEntity<List<DashboardDTO.PendingReportItem>> getPendingReports() {
        return ResponseEntity.ok(dashboardService.getPendingReports());
    }

    @GetMapping("/transaction-logs")
    public ResponseEntity<List<DashboardDTO.TransactionLogItem>> getTransactionLogs() {
        return ResponseEntity.ok(dashboardService.getTransactionLogs());
    }

    @GetMapping("/recent-products")
    public ResponseEntity<List<DashboardDTO.RecentProductItem>> getRecentProducts() {
        return ResponseEntity.ok(dashboardService.getRecentProducts());
    }
}
