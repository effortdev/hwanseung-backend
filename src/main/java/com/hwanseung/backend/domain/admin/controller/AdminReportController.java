package com.hwanseung.backend.domain.admin.controller;

import com.hwanseung.backend.domain.admin.dto.ReportDTO.*;
import com.hwanseung.backend.domain.admin.service.AdminReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final AdminReportService reportService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "") String type) {
        return ResponseEntity.ok(reportService.getReports(page, size, keyword, status, type));
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<DetailResponse> getReportDetail(@PathVariable Long reportId) {
        return ResponseEntity.ok(reportService.getReportDetail(reportId));
    }

    @PatchMapping("/{reportId}/warn")
    public ResponseEntity<Void> warnUser(
            @PathVariable Long reportId,
            @RequestBody MemoRequest request) {
        // TODO: SecurityContext에서 관리자 닉네임 추출
        String adminNickname = "관리자";
        reportService.warnUser(reportId, request.getMemo(), adminNickname);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{reportId}/suspend")
    public ResponseEntity<Void> suspendUser(
            @PathVariable Long reportId,
            @RequestBody SuspendRequest request) {
        String adminNickname = "관리자";
        reportService.suspendUser(reportId, request.getDays(), request.getMemo(), adminNickname);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{reportId}/dismiss")
    public ResponseEntity<Void> dismissReport(
            @PathVariable Long reportId,
            @RequestBody MemoRequest request) {
        String adminNickname = "관리자";
        reportService.dismissReport(reportId, request.getMemo(), adminNickname);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{reportId}/content")
    public ResponseEntity<Void> deleteReportedContent(@PathVariable Long reportId) {
        reportService.deleteReportedContent(reportId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/suspended-users")
    public ResponseEntity<Map<String, Object>> getSuspendedUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword) {
        return ResponseEntity.ok(reportService.getSuspendedUsers(page, size, keyword));
    }
}
