package com.hwanseung.backend.domain.admin.service;

import com.hwanseung.backend.domain.admin.controller.LoginManager;
import com.hwanseung.backend.domain.admin.dto.StatisticsDTO.*;
import com.hwanseung.backend.domain.admin.dto.Status;
import com.hwanseung.backend.domain.admin.repository.AdminReportRepository;
import com.hwanseung.backend.domain.admin.repository.SearchKeywordRepository;
import com.hwanseung.backend.domain.product.repository.ProductRepository;
import com.hwanseung.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminStatisticsService {

    // ※ 아래 Repository들은 기존 프로젝트에 이미 존재하는 것을 주입받는다고 가정합니다.
    //    없는 경우 해당 Repository를 추가로 만들거나, 직접 쿼리를 작성하세요.
    private final UserRepository userRepository;           // 기존 User 엔티티 Repository
    private final ProductRepository productRepository;     // 기존 Product 엔티티 Repository
    private final AdminReportRepository reportRepository;
    private final SearchKeywordRepository searchKeywordRepository;

    // private final TransactionRepository transactionRepository;  // 거래 엔티티가 있다면
    // private final WishlistRepository wishlistRepository;        // 찜 엔티티가 있다면

    // LoginManager 의존성 주입 추가
    private final LoginManager loginManager;

    /** 1. 실시간 접속자 수 */
    public OnlineUsersResponse getOnlineUsers() {
        // 하드코딩된 0을 지우고, LoginManager에서 실제 카운트를 가져옴
        int count = loginManager.getActiveUserCount();
        return OnlineUsersResponse.builder().count(count).build();
    }

    /** 2. 사용자 통계 */
    public UserStatsResponse getUserStats() {
        long totalUsers = userRepository.count();

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        // ※ UserRepository에 아래 메서드 추가 필요:
        //   long countByCreatedAtAfter(LocalDateTime dateTime);
        long dailyNewUsers = userRepository.countByCreatedAtAfter(todayStart);
        long monthlyNewUsers = userRepository.countByCreatedAtAfter(monthStart);

        return UserStatsResponse.builder()
                .totalUsers(totalUsers)
                .dailyNewUsers(dailyNewUsers)
                .monthlyNewUsers(monthlyNewUsers)
                .build();
    }

    /** 3. 거래 통계 */
    public TransactionStatsResponse getTransactionStats() {
        // TODO: Transaction 엔티티/Repository가 있다면 실제 데이터 조회
        // 예시:
        // long totalTx = transactionRepository.count();
        // long totalGMV = transactionRepository.sumTotalPrice();
        // long dailyTx = transactionRepository.countByCreatedAtAfter(todayStart);
        // long monthlyTx = transactionRepository.countByCreatedAtAfter(monthStart);

        return TransactionStatsResponse.builder()
                .totalTransactions(0)
                .totalGMV(0)
                .dailyTransactions(0)
                .monthlyTransactions(0)
                .build();
    }

    /** 4. 상품 통계 */
    public ProductStatsResponse getProductStats() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime weekStart = LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();

        long totalListings = productRepository.count();

        // ※ ProductRepository에 아래 메서드 추가 필요:
        //   long countByCreatedAtAfter(LocalDateTime dateTime);
        long dailyListings = productRepository.countByCreatedAtAfter(todayStart);
        long weeklyListings = productRepository.countByCreatedAtAfter(weekStart);
        long monthlyListings = productRepository.countByCreatedAtAfter(monthStart);

        // 카테고리별 분포
        // ※ ProductRepository에 추가 필요:
        //   @Query("SELECT p.category, COUNT(p) FROM Product p GROUP BY p.category ORDER BY COUNT(p) DESC")
        //   List<Object[]> countByCategory();
        List<CategoryCount> categoryDistribution = productRepository.countByCategory()
                .stream()
                .map(row -> new CategoryCount((String) row[0], (Long) row[1]))
                .collect(Collectors.toList());

        // 가격 분포
        // ※ ProductRepository에 추가 필요:
        //   long countByPriceLessThanEqual(int price);
        //   long countByPriceBetween(int min, int max);
        //   long countByPriceGreaterThan(int price);
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

    /** 5. 검색 & 탐색 통계 */
    public SearchStatsResponse getSearchStats() {
        List<KeywordCount> keywords = searchKeywordRepository.findTop10ByOrderByCountDesc()
                .stream()
                .map(sk -> new KeywordCount(sk.getKeyword(), sk.getCount()))
                .collect(Collectors.toList());

        // TODO: Wishlist 엔티티가 있다면 실제 데이터 조회
        // long totalWishlist = wishlistRepository.count();
        // long dailyWishlist = wishlistRepository.countByCreatedAtAfter(todayStart);

        return SearchStatsResponse.builder()
                .popularKeywords(keywords)
                .totalWishlist(0)
                .dailyWishlist(0)
                .build();
    }

    /** 6. 신고 통계 */
    public ReportStatsResponse getReportStats() {
        long totalReports = reportRepository.count();
        long pendingReports = reportRepository.countByStatus("PENDING");
        long resolvedReports = reportRepository.countByStatus("RESOLVED")
                + reportRepository.countByStatus("WARNED")
                + reportRepository.countByStatus("SUSPENDED");
        long dismissed = reportRepository.countByStatus("DISMISSED");

        // ※ UserRepository에 추가 필요:
        //   long countByStatus(String status);
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
