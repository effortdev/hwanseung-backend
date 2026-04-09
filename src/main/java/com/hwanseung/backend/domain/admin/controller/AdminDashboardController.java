package com.hwanseung.backend.domain.admin.controller;

import com.hwanseung.backend.domain.admin.dto.DashboardDTO.*;
import com.hwanseung.backend.domain.admin.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
