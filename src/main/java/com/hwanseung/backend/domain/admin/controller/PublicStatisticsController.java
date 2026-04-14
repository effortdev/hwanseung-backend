package com.hwanseung.backend.domain.admin.controller;

import com.hwanseung.backend.domain.admin.service.PublicStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 메인 페이지용 공개 통계 API
 * - 인증 불필요 (SecurityConfig에서 permitAll 설정 필요)
 * - 누적 거래 금액(GMV)과 오늘 거래 수만 반환
 */
@RestController
@RequestMapping("/api/public/statistics")
@RequiredArgsConstructor
public class PublicStatisticsController {

    private final PublicStatisticsService publicStatisticsService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPublicStats() {
        Map<String, Object> stats = publicStatisticsService.getPublicStats();
        return ResponseEntity.ok(stats);
    }
}
