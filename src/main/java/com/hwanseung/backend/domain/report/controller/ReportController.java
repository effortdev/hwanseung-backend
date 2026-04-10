package com.hwanseung.backend.domain.report.controller;

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