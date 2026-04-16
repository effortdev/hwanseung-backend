package com.hwanseung.backend.domain.admin.service;

import com.hwanseung.backend.domain.admin.dto.DashboardDTO;
import com.hwanseung.backend.domain.admin.entity.Report;
import com.hwanseung.backend.domain.product.entity.Product;
import jakarta.persistence.TypedQuery;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final EntityManager entityManager;
    private static final DateTimeFormatter LOG_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<DashboardDTO.PendingReportItem> getPendingReports() {
        try {
            TypedQuery<Report> query = entityManager.createQuery(
                    "SELECT r FROM Report r WHERE r.status = 'PENDING' ORDER BY r.createdAt DESC",
                    Report.class
            );
            query.setMaxResults(7);

            return query.getResultList().stream()
                    .map(r -> DashboardDTO.PendingReportItem.builder()
                            .id(r.getId())
                            .reasonCategory(r.getReasonCategory())
                            .reportedNickname(r.getReportedNickname())
                            .status(r.getStatus())
                            .createdAt(r.getCreatedAt() != null
                                    ? r.getCreatedAt().format(LOG_FORMATTER) : "")
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<DashboardDTO.TransactionLogItem> getTransactionLogs() {
        try {
            TypedQuery<Product> query = entityManager.createQuery(
                    "SELECT p FROM Product p " +
                            "WHERE p.saleStatus = 'SOLD_OUT' AND p.deletedAt IS NULL " +
                            "ORDER BY p.updatedAt DESC",
                    Product.class
            );
            query.setMaxResults(10);

            return query.getResultList().stream()
                    .map(p -> DashboardDTO.TransactionLogItem.builder()
                            .productId(p.getProductId())
                            .title(p.getTitle())
                            .sellerNickname(p.getSellerNickname())
                            .price(p.getPrice())
                            .completedAt(p.getUpdatedAt() != null
                                    ? p.getUpdatedAt().format(LOG_FORMATTER) : "")
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<DashboardDTO.RecentProductItem> getRecentProducts() {
        try {
            TypedQuery<Product> query = entityManager.createQuery(
                    "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.productImages " +
                            "WHERE p.deletedAt IS NULL " +
                            "ORDER BY p.createdAt DESC",
                    Product.class
            );
            query.setMaxResults(6);

            return query.getResultList().stream()
                    .map(p -> {
                        String firstImage = null;
                        if (p.getProductImages() != null && !p.getProductImages().isEmpty()) {
                            firstImage = p.getProductImages().get(0).getImagePath();
                        }
                        return DashboardDTO.RecentProductItem.builder()
                                .productId(p.getProductId())
                                .title(p.getTitle())
                                .price(p.getPrice())
                                .imageUrl(firstImage)
                                .createdAt(p.getCreatedAt() != null
                                        ? p.getCreatedAt().format(LOG_FORMATTER) : "")
                                .build();
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public WeeklyTrendResponse getWeeklyTrend() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);
        LocalDateTime startDateTime = startDate.atStartOfDay();

        DateTimeFormatter labelFormatter = DateTimeFormatter.ofPattern("MM/dd");

        List<String> labels = new ArrayList<>();
        Map<String, Long> transactionMap = new HashMap<>();
        Map<String, Long> signupMap = new HashMap<>();

        for (int i = 0; i < 7; i++) {
            String label = startDate.plusDays(i).format(labelFormatter);
            labels.add(label);
            transactionMap.put(label, 0L);
            signupMap.put(label, 0L);
        }

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
        }

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
        }

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

    public SummaryResponse getDashboardSummary() {
        long activeTransactions = 0;
        long completedTransactions = 0;
        long pendingReports = 0;

        try {
            Query activeQuery = entityManager.createQuery(
                "SELECT COUNT(p) FROM Product p WHERE p.saleStatus = 'RESERVED'"
            );
            activeTransactions = (Long) activeQuery.getSingleResult();
        } catch (Exception e) {
        }

        try {
            Query completedQuery = entityManager.createQuery(
                "SELECT COUNT(p) FROM Product p WHERE p.saleStatus = 'SOLD_OUT'"
            );
            completedTransactions = (Long) completedQuery.getSingleResult();
        } catch (Exception e) {
        }

        try {
            Query reportQuery = entityManager.createQuery(
                "SELECT COUNT(r) FROM Report r WHERE r.status = 'PENDING'"
            );
            pendingReports = (Long) reportQuery.getSingleResult();
        } catch (Exception e) {
        }

        return SummaryResponse.builder()
                .activeTransactions(activeTransactions)
                .completedTransactions(completedTransactions)
                .pendingReports(pendingReports)
                .build();
    }
}
