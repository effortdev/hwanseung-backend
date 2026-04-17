package com.hwanseung.backend.domain.admin.controller;

import com.hwanseung.backend.domain.admin.service.PublicStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
