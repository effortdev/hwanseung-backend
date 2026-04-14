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

    /** 2. 사용자 통계 */
    @GetMapping("/users")
    public ResponseEntity<UserStatsResponse> getUserStats() {
        return ResponseEntity.ok(statisticsService.getUserStats());
    }

    /** 3. 거래 통계 */
    @GetMapping("/transactions")
    public ResponseEntity<TransactionStatsResponse> getTransactionStats() {
        return ResponseEntity.ok(statisticsService.getTransactionStats());
    }

    /** 4. 상품 통계 */
    @GetMapping("/products")
    public ResponseEntity<ProductStatsResponse> getProductStats() {
        return ResponseEntity.ok(statisticsService.getProductStats());
    }

    /** 5. 검색 & 탐색 통계 */
    @GetMapping("/search")
    public ResponseEntity<SearchStatsResponse> getSearchStats() {
        return ResponseEntity.ok(statisticsService.getSearchStats());
    }

    /** 6. 신고 통계 */
    @GetMapping("/reports")
    public ResponseEntity<ReportStatsResponse> getReportStats() {
        return ResponseEntity.ok(statisticsService.getReportStats());
    }
}
