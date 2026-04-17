package com.hwanseung.backend.domain.user.service;

import com.hwanseung.backend.domain.user.entity.TrustScoreHistory;
import com.hwanseung.backend.domain.user.entity.User;
import com.hwanseung.backend.domain.user.repository.TrustScoreHistoryRepository;
import com.hwanseung.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrustScoreService {

    private final UserRepository userRepository;
    private final TrustScoreHistoryRepository historyRepository;

    // 점수 변동 로직 (타 서비스에서 호출)
    @Transactional
    public void updateTrustScore(Long userId, int scoreChange, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 유저 점수 업데이트
        user.setTrustScore(user.getTrustScore() + scoreChange);

        // 내역 기록
        TrustScoreHistory history = TrustScoreHistory.builder()
                .user(user)
                .scoreChange(scoreChange)
                .reason(reason)
                .build();

        historyRepository.save(history);
    }

    // 내역 조회 로직
    @Transactional(readOnly = true)
    public List<TrustScoreHistory> getHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return historyRepository.findByUserOrderByCreatedAtDesc(user);
    }
}
