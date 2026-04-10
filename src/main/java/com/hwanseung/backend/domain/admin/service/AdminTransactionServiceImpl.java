package com.hwanseung.backend.domain.admin.service;

import com.hwanseung.backend.domain.admin.dto.TopCategoryDTO;
import com.hwanseung.backend.domain.admin.dto.TransactionListItemDTO;
import com.hwanseung.backend.domain.admin.dto.TransactionSeriesPointDTO;
import com.hwanseung.backend.domain.admin.dto.TransactionSeriesResponse;
import com.hwanseung.backend.domain.admin.dto.TransactionStatusCountDTO;
import com.hwanseung.backend.domain.admin.dto.TransactionSummaryDTO;
import com.hwanseung.backend.domain.admin.repository.AdminTransactionQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Product 테이블을 거래 관점으로 조회하는 관리자 서비스 구현체.
 *
 *  - "완료된 거래" 기준: sale_status = 'SOLD_OUT'
 *  - amount(금액) 기준:  price 컬럼
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminTransactionServiceImpl implements AdminTransactionService {

    private final AdminTransactionQueryRepository repo;

    private static String bucketFormat(String period) {
        if (period == null) {
            return "%Y-%m-%d";
        }
        return switch (period) {
            case "monthly" -> "%Y-%m";
            case "weekly"  -> "%x-W%v"; // ISO week
            default        -> "%Y-%m-%d";
        };
    }

    private static long toLong(Object o) {
        return o == null ? 0L : ((Number) o).longValue();
    }

    private static LocalDateTime toLocalDateTime(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Timestamp ts) {
            return ts.toLocalDateTime();
        }
        if (o instanceof LocalDateTime ldt) {
            return ldt;
        }
        // 일부 드라이버가 java.util.Date 등을 반환할 수 있어 방어
        if (o instanceof java.util.Date d) {
            return new Timestamp(d.getTime()).toLocalDateTime();
        }
        throw new IllegalStateException("Unsupported datetime type: " + o.getClass());
    }

    @Override
    public TransactionSeriesResponse getSeries(String period, LocalDate startDate, LocalDate endDate) {
        LocalDateTime from = startDate.atStartOfDay();
        LocalDateTime to   = endDate.plusDays(1).atStartOfDay();

        List<Object[]> rows = repo.findSeries(bucketFormat(period), from, to);
        List<TransactionSeriesPointDTO> points = rows.stream()
                .map(r -> new TransactionSeriesPointDTO(
                        (String) r[0], toLong(r[1]), toLong(r[2])))
                .toList();

        long totalCount  = points.stream().mapToLong(TransactionSeriesPointDTO::count).sum();
        long totalAmount = points.stream().mapToLong(TransactionSeriesPointDTO::amount).sum();
        long avg = totalCount == 0 ? 0 : totalAmount / totalCount;

        return new TransactionSeriesResponse(period, points,
                new TransactionSummaryDTO(totalCount, totalAmount, avg));
    }

    @Override
    public List<TransactionStatusCountDTO> getStatusBreakdown(LocalDate startDate, LocalDate endDate) {
        return repo.findStatusBreakdown(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay())
                .stream()
                .map(r -> new TransactionStatusCountDTO((String) r[0], toLong(r[1]), toLong(r[2])))
                .toList();
    }

    @Override
    public List<TopCategoryDTO> getTopCategories(LocalDate startDate, LocalDate endDate, int limit) {
        return repo.findTopCategories(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay(), limit)
                .stream()
                .map(r -> new TopCategoryDTO((String) r[0], toLong(r[1]), toLong(r[2])))
                .toList();
    }

    @Override
    public Page<TransactionListItemDTO> getList(LocalDate startDate, LocalDate endDate, int page, int size) {
        LocalDateTime from = startDate.atStartOfDay();
        LocalDateTime to   = endDate.plusDays(1).atStartOfDay();

        List<Object[]> rows = repo.findList(from, to, size, page * size);
        List<TransactionListItemDTO> content = rows.stream()
                .map(r -> new TransactionListItemDTO(
                        ((Number) r[0]).intValue(),   // product_id
                        (String) r[1],                 // seller_id
                        (String) r[2],                 // seller_nickname
                        (String) r[3],                 // title
                        (String) r[4],                 // category
                        toLong(r[5]),                  // price -> amount
                        (String) r[6],                 // sale_status -> status
                        toLocalDateTime(r[7])))        // created_at
                .toList();

        long total = repo.countList(from, to);
        return new PageImpl<>(content, PageRequest.of(page, size), total);
    }
}
