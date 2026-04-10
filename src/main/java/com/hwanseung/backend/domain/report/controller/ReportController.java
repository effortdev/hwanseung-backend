package com.hwanseung.backend.domain.report.controller;

import com.hwanseung.backend.domain.report.dto.ReportCheckResponseDTO;    // ✅추가
import com.hwanseung.backend.domain.report.dto.ReportCreateRequestDTO;
import com.hwanseung.backend.domain.report.dto.ReportCreateResponseDTO;
import com.hwanseung.backend.domain.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // 상세페이지에서 신고 버튼 누를 때 중복 신고 여부 확인
    @GetMapping("/products/{productId}/check")
    public ResponseEntity<ReportCheckResponseDTO> checkProductReport(
            @PathVariable Long productId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                reportService.checkProductReport(productId, authentication)
        );
    }

    @PostMapping("/products/{productId}")
    public ResponseEntity<ReportCreateResponseDTO> report(
            @PathVariable Long productId,
            @RequestBody ReportCreateRequestDTO dto,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                reportService.createProductReport(productId, dto, authentication)
        );
    }
}