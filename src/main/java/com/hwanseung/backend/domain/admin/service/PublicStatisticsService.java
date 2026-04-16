package com.hwanseung.backend.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicStatisticsService {

        private final AdminStatisticsService adminStatisticsService;

        public Map<String, Object> getPublicStats() {
            var txStats = adminStatisticsService.getTransactionStats();
            Map<String, Object> result = new HashMap<>();
            result.put("totalGMV", txStats.getTotalGMV());
            result.put("dailyTransactions", txStats.getDailyTransactions());
            return result;
        }


}
