package com.hwanseung.backend.domain.admin.service;

import com.hwanseung.backend.domain.admin.dto.DashboardDTO.SummaryResponse;
import com.hwanseung.backend.domain.admin.dto.DashboardDTO.WeeklyTrendResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final EntityManager entityManager;

    /**
     * 최근 7일간 일별 거래 건수 + 가입 건수 조회
     *
     * - transactions: Product 테이블에서 sale_status가 변경된 건 또는 거래 테이블 기준
     *   (프로젝트 구조에 따라 쿼리 수정 필요)
     * - signups: User 테이블의 created_at 기준 일별 가입 수
     */
    public WeeklyTrendResponse getWeeklyTrend() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6); // 오늘 포함 7일
        LocalDateTime startDateTime = startDate.atStartOfDay();

        DateTimeFormatter labelFormatter = DateTimeFormatter.ofPattern("MM/dd");

        // 7일치 날짜 라벨 생성
        List<String> labels = new ArrayList<>();
        Map<String, Long> transactionMap = new HashMap<>();
        Map<String, Long> signupMap = new HashMap<>();

        for (int i = 0; i < 7; i++) {
            String label = startDate.plusDays(i).format(labelFormatter);
            labels.add(label);
            transactionMap.put(label, 0L);
            signupMap.put(label, 0L);
        }

        // 일별 가입 수 조회
        // User 엔티티의 created_at 컬럼 기준
        try {
            Query signupQuery = entityManager.createQuery(
                "SELECT FUNCTION('DATE_FORMAT', u.createdAt, '%m/%d') AS day, COUNT(u) " +
                "FROM User u " +
                "WHERE u.createdAt >= :startDate " +
                "GROUP BY FUNCTION('DATE_FORMAT', u.createdAt, '%m/%d') " +
                "ORDER BY day"
            );
            signupQuery.setParameter("startDate", startDateTime);

            @SuppressWarnings("unchecked")
            List<Object[]> signupResults = signupQuery.getResultList();
            for (Object[] row : signupResults) {
                String day = (String) row[0];
                Long count = (Long) row[1];
                if (signupMap.containsKey(day)) {
                    signupMap.put(day, count);
                }
            }
        } catch (Exception e) {
            // User 엔티티 구조가 다를 경우 로깅 후 0으로 유지
            // TODO: 기존 프로젝트의 User 엔티티에 맞게 수정
        }

        // 일별 거래 건수 조회
        // Product의 sale_status = 'SOLD_OUT'인 건의 updated_at 기준
        // 또는 별도 Transaction 테이블이 있다면 해당 테이블 기준으로 변경
        try {
            Query transactionQuery = entityManager.createQuery(
                "SELECT FUNCTION('DATE_FORMAT', p.updatedAt, '%m/%d') AS day, COUNT(p) " +
                "FROM Product p " +
                "WHERE p.saleStatus = 'SOLD_OUT' " +
                "AND p.updatedAt >= :startDate " +
                "GROUP BY FUNCTION('DATE_FORMAT', p.updatedAt, '%m/%d') " +
                "ORDER BY day"
            );
            transactionQuery.setParameter("startDate", startDateTime);

            @SuppressWarnings("unchecked")
            List<Object[]> txResults = transactionQuery.getResultList();
            for (Object[] row : txResults) {
                String day = (String) row[0];
                Long count = (Long) row[1];
                if (transactionMap.containsKey(day)) {
                    transactionMap.put(day, count);
                }
            }
        } catch (Exception e) {
            // Product 엔티티 구조가 다를 경우 로깅 후 0으로 유지
            // TODO: 기존 프로젝트의 Product 엔티티/Transaction 테이블에 맞게 수정
        }

        // Map → 순서 보장 List 변환
        List<Long> transactions = new ArrayList<>();
        List<Long> signups = new ArrayList<>();
        for (String label : labels) {
            transactions.add(transactionMap.getOrDefault(label, 0L));
            signups.add(signupMap.getOrDefault(label, 0L));
        }

        return WeeklyTrendResponse.builder()
                .labels(labels)
                .transactions(transactions)
                .signups(signups)
                .build();
    }

    /**
     * 대시보드 요약 카드 데이터 (진행 중 거래, 거래 완료, 미처리 신고)
     * TODO: 기존 프로젝트의 Product/Transaction, Report 엔티티에 맞게 수정
     */
    public SummaryResponse getDashboardSummary() {
        long activeTransactions = 0;
        long completedTransactions = 0;
        long pendingReports = 0;

        try {
            // 진행 중 거래 (sale_status = 'SALE' 또는 거래중 상태)
            Query activeQuery = entityManager.createQuery(
                "SELECT COUNT(p) FROM Product p WHERE p.saleStatus = 'RESERVED'"
            );
            activeTransactions = (Long) activeQuery.getSingleResult();
        } catch (Exception e) {
            // TODO: 기존 엔티티 구조에 맞게 수정
        }

        try {
            // 거래 완료
            Query completedQuery = entityManager.createQuery(
                "SELECT COUNT(p) FROM Product p WHERE p.saleStatus = 'SOLD_OUT'"
            );
            completedTransactions = (Long) completedQuery.getSingleResult();
        } catch (Exception e) {
            // TODO: 기존 엔티티 구조에 맞게 수정
        }

        try {
            // 미처리 신고
            Query reportQuery = entityManager.createQuery(
                "SELECT COUNT(r) FROM Report r WHERE r.status = 'PENDING'"
            );
            pendingReports = (Long) reportQuery.getSingleResult();
        } catch (Exception e) {
            // TODO: Report 엔티티가 없을 경우
        }

        return SummaryResponse.builder()
                .activeTransactions(activeTransactions)
                .completedTransactions(completedTransactions)
                .pendingReports(pendingReports)
                .build();
    }
}
