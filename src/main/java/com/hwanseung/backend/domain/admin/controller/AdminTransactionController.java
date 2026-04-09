package com.hwanseung.backend.domain.admin.controller;

import com.hwanseung.backend.domain.admin.dto.TopCategoryDTO;
import com.hwanseung.backend.domain.admin.dto.TransactionListItemDTO;
import com.hwanseung.backend.domain.admin.dto.TransactionSeriesResponse;
import com.hwanseung.backend.domain.admin.dto.TransactionStatusCountDTO;
import com.hwanseung.backend.domain.admin.service.AdminTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * 관리자 거래 통계/목록 API.
 * Product 테이블을 거래 관점으로 조회한다.
 */
@RestController
@RequestMapping("/api/admin/transactions")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER', 'SUB')")
public class AdminTransactionController {

    private final AdminTransactionService service;

    /** 일/주/월 시계열 통계 (완료된 거래 기준) */
    @GetMapping("/series")
    public TransactionSeriesResponse getSeries(
            @RequestParam String period,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return service.getSeries(period, startDate, endDate);
    }

    /** 판매 상태별 분포 (SALE / SOLD_OUT) */
    @GetMapping("/status-breakdown")
    public List<TransactionStatusCountDTO> getStatusBreakdown(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return service.getStatusBreakdown(startDate, endDate);
    }

    /** 카테고리별 거래 TOP N */
    @GetMapping("/top-categories")
    public List<TopCategoryDTO> getTopCategories(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "8") int limit) {
        return service.getTopCategories(startDate, endDate, limit);
    }

    /** 거래 내역 페이지 */
    @GetMapping
    public Page<TransactionListItemDTO> getList(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.getList(startDate, endDate, page, size);
    }
}
