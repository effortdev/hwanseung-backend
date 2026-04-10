package com.hwanseung.backend.domain.admin.service;

import com.hwanseung.backend.domain.admin.dto.TopCategoryDTO;
import com.hwanseung.backend.domain.admin.dto.TransactionListItemDTO;
import com.hwanseung.backend.domain.admin.dto.TransactionSeriesResponse;
import com.hwanseung.backend.domain.admin.dto.TransactionStatusCountDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

/**
 * 관리자 거래 통계/목록 서비스.
 * Product 테이블을 거래 관점으로 조회한다.
 */
public interface AdminTransactionService {
    TransactionSeriesResponse getSeries(String period, LocalDate startDate, LocalDate endDate);
    List<TransactionStatusCountDTO> getStatusBreakdown(LocalDate startDate, LocalDate endDate);
    List<TopCategoryDTO> getTopCategories(LocalDate startDate, LocalDate endDate, int limit);
    Page<TransactionListItemDTO> getList(LocalDate startDate, LocalDate endDate, int page, int size);
}
