package com.hwanseung.backend.domain.admin.service;

import com.hwanseung.backend.domain.admin.dto.StatisticsDTO.*;
import com.hwanseung.backend.domain.admin.dto.Status;
import com.hwanseung.backend.domain.admin.repository.AdminReportRepository;
import com.hwanseung.backend.domain.admin.repository.SearchKeywordRepository;
import com.hwanseung.backend.domain.admin.repository.TransactionRepository;
import com.hwanseung.backend.domain.product.repository.ProductLikeRepository;
import com.hwanseung.backend.domain.product.repository.ProductRepository;
import com.hwanseung.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminStatisticsService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final AdminReportRepository reportRepository;
    private final SearchKeywordRepository searchKeywordRepository;

     private final TransactionRepository transactionRepository;
     private final ProductLikeRepository wishlistRepository;

    public UserStatsResponse getUserStats() {
        long totalUsers = userRepository.count();

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        long dailyNewUsers = userRepository.countByCreatedAtAfter(todayStart);
        long monthlyNewUsers = userRepository.countByCreatedAtAfter(monthStart);

        return UserStatsResponse.builder()
                .totalUsers(totalUsers)
                .dailyNewUsers(dailyNewUsers)
                .monthlyNewUsers(monthlyNewUsers)
                .build();
    }

    @Transactional(readOnly = true)
    public TransactionStatsResponse getTransactionStats() {
        LocalDate today = LocalDate.now();

        LocalDateTime startOfToday = today.atStartOfDay();

        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();

        long totalTx = transactionRepository.count();
        long totalGMV = transactionRepository.sumTotalPrice();

        long dailyTx = transactionRepository.countByCreatedAtGreaterThanEqual(startOfToday);
        long monthlyTx = transactionRepository.countByCreatedAtGreaterThanEqual(startOfMonth);

        return TransactionStatsResponse.builder()
                .totalTransactions(totalTx)
                .totalGMV(totalGMV)
                .dailyTransactions(dailyTx)
                .monthlyTransactions(monthlyTx)
                .build();
    }

    public ProductStatsResponse getProductStats() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime weekStart = LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        long totalListings = productRepository.count();

        long dailyListings = productRepository.countByCreatedAtAfter(todayStart);
        long weeklyListings = productRepository.countByCreatedAtAfter(weekStart);
        long monthlyListings = productRepository.countByCreatedAtAfter(monthStart);

        List<CategoryCount> categoryDistribution = productRepository.countByCategory()
                .stream()
                .map(row -> new CategoryCount((String) row[0], (Long) row[1]))
                .collect(Collectors.toList());

        long lowPrice = productRepository.countByPriceLessThanEqual(50000);
        long midPrice = productRepository.countByPriceBetween(50001, 300000);
        long highPrice = productRepository.countByPriceGreaterThan(300000);

        List<PriceRange> priceDistribution = List.of(
                new PriceRange("저가 (~5만원)", lowPrice),
                new PriceRange("중가 (5~30만원)", midPrice),
                new PriceRange("고가 (30만원~)", highPrice)
        );

        return ProductStatsResponse.builder()
                .dailyListings(dailyListings)
                .weeklyListings(weeklyListings)
                .monthlyListings(monthlyListings)
                .totalListings(totalListings)
                .categoryDistribution(categoryDistribution)
                .priceDistribution(priceDistribution)
                .build();
    }

    public SearchStatsResponse getSearchStats() {
        List<KeywordCount> keywords = searchKeywordRepository.findTop10ByOrderByCountDesc()
                .stream()
                .map(sk -> new KeywordCount(sk.getKeyword(), sk.getCount()))
                .collect(Collectors.toList());

         long totalWishlist = wishlistRepository.count();

        return SearchStatsResponse.builder()
                .popularKeywords(keywords)
                .totalWishlist(totalWishlist)
                .build();
    }

    public ReportStatsResponse getReportStats() {
        long totalReports = reportRepository.count();
        long pendingReports = reportRepository.countByStatus("PENDING");
        long resolvedReports = reportRepository.countByStatus("RESOLVED")
                + reportRepository.countByStatus("WARNED")
                + reportRepository.countByStatus("SUSPENDED");
        long dismissed = reportRepository.countByStatus("DISMISSED");

        long suspendedUsers = userRepository.countByStatus(Status.valueOf("SUSPENDED"));
        long blockedUsers = userRepository.countByStatus(Status.valueOf("BLOCKED"));

        return ReportStatsResponse.builder()
                .totalReports(totalReports)
                .pendingReports(pendingReports)
                .resolvedReports(resolvedReports + dismissed)
                .blockedUsers(blockedUsers)
                .suspendedUsers(suspendedUsers)
                .build();
    }
}
