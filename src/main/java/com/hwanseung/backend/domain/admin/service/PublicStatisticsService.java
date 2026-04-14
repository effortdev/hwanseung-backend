package com.hwanseung.backend.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 메인 페이지 공개 통계 서비스
 *
 * ※ 기존 admin 통계에서 사용하는 Repository를 그대로 주입받아 사용합니다.
 *   아래 TODO 주석을 확인하고, 본인 프로젝트의 실제 Repository/Service 이름에 맞게 수정하세요.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicStatisticsService {

    // ─── TODO: 본인 프로젝트의 실제 Repository 이름으로 교체 ───
    // 예시 1) TransactionRepository를 직접 사용하는 경우:
    //   private final TransactionRepository transactionRepository;
    //
    // 예시 2) 기존 AdminStatisticsService를 재활용하는 경우:
    // ──────────────────────────────────────────────────────────

    // ── 아래는 "예시 2) 기존 서비스 재활용" 방식 기준 코드입니다 ──
    // 만약 기존 거래 통계 서비스 클래스가 이미 있으면 그것을 주입하세요.
    // 클래스 이름이 다르면 import와 필드명만 바꾸면 됩니다.


      // ■ 방법 A : 기존 AdminStatisticsService 재활용

        private final AdminStatisticsService adminStatisticsService;

        public Map<String, Object> getPublicStats() {
           // 기존 서비스의 거래 통계 메서드 호출
            var txStats = adminStatisticsService.getTransactionStats();
            Map<String, Object> result = new HashMap<>();
            result.put("totalGMV", txStats.getTotalGMV());               // 누적 거래 금액 (원)
            result.put("dailyTransactions", txStats.getDailyTransactions()); // 오늘 거래 수
            return result;
        }

    /*
     * ■ 방법 B : TransactionRepository 직접 사용 (JPQL 쿼리)
     *
     *   private final TransactionRepository transactionRepository;
     *
     *   public Map<String, Object> getPublicStats() {
     *       Long totalGMV = transactionRepository.sumTotalGMV();        // 전체 GMV
     *       Long dailyTx  = transactionRepository.countTodayTransactions(); // 오늘 거래 수
     *
     *       Map<String, Object> result = new HashMap<>();
     *       result.put("totalGMV", totalGMV != null ? totalGMV : 0L);
     *       result.put("dailyTransactions", dailyTx != null ? dailyTx : 0L);
     *       return result;
     *   }
     *
     *   // TransactionRepository에 추가할 쿼리 메서드:
     *   // @Query("SELECT COALESCE(SUM(t.price), 0) FROM Transaction t WHERE t.status = 'COMPLETED'")
     *   // Long sumTotalGMV();
     *   //
     *   // @Query("SELECT COUNT(t) FROM Transaction t WHERE DATE(t.createdAt) = CURRENT_DATE")
     *   // Long countTodayTransactions();
     */

    // ─── 아래는 방법 B 기준 예시 구현입니다. 실제 Repository에 맞게 수정하세요 ───

    // TODO: 아래 주석을 해제하고 본인의 Repository를 주입하세요
    // private final TransactionRepository transactionRepository;

}
