package com.hwanseung.backend.domain.admin.controller;

import com.hwanseung.backend.domain.admin.dto.StatisticsDTO.*;
import com.hwanseung.backend.domain.admin.service.AdminStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
public class AdminStatisticsController {
    private final AdminStatisticsService statisticsService;

    @GetMapping("/users")
    public ResponseEntity<UserStatsResponse> getUserStats() {
        return ResponseEntity.ok(statisticsService.getUserStats());
    }

    @GetMapping("/transactions")
    public ResponseEntity<TransactionStatsResponse> getTransactionStats() {
        return ResponseEntity.ok(statisticsService.getTransactionStats());
    }

    @GetMapping("/products")
    public ResponseEntity<ProductStatsResponse> getProductStats() {
        return ResponseEntity.ok(statisticsService.getProductStats());
    }

    @GetMapping("/search")
    public ResponseEntity<SearchStatsResponse> getSearchStats() {
        return ResponseEntity.ok(statisticsService.getSearchStats());
    }

    @GetMapping("/reports")
    public ResponseEntity<ReportStatsResponse> getReportStats() {
        return ResponseEntity.ok(statisticsService.getReportStats());
    }
}
