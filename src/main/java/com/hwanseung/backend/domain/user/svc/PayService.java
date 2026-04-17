package com.hwanseung.backend.domain.user.svc;

import com.hwanseung.backend.domain.user.controller.PayBalanceRepository;
import com.hwanseung.backend.domain.user.controller.PayHistoryRepository;
import com.hwanseung.backend.domain.user.dto.PayBalance;
import com.hwanseung.backend.domain.user.dto.PayHistory;
import com.hwanseung.backend.domain.user.dto.PayUseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PayService {

    private final PayHistoryRepository payHistoryRepository;
    private final PayBalanceRepository payBalanceRepository;

    @Transactional
    public boolean chargeHwanseungPay(PayHistory history) {
        try {
            payHistoryRepository.save(history);

            // 2. 해당 유저의 통장(Balance) 찾기
            PayBalance balance = payBalanceRepository.findByUserId(history.getUserId())
                    .orElseGet(() -> {
                        // 통장이 없는 신규 유저라면 새로 생성
                        PayBalance newBalance = PayBalance.builder().userId(history.getUserId()).hwanseungPay(0).build();
                        return newBalance;
                    });

            int currentPay = balance.getHwanseungPay();
            balance.setHwanseungPay(currentPay + history.getAmount());

            payBalanceRepository.save(balance);

            return true;
        } catch (Exception e) {
            throw new RuntimeException("DB 저장 실패로 인한 결제 취소");
        }
    }
    @Transactional
    public boolean useHwanseungPay(String userId, PayUseVO useVO) {
        // 1. 내 통장(Balance) 찾기
        PayBalance balance = payBalanceRepository.findByUserId(Long.valueOf(userId))
                .orElseThrow(() -> new IllegalArgumentException("지갑 정보가 존재하지 않습니다."));

        if (balance.getHwanseungPay() < useVO.getAmount()) {
            throw new IllegalArgumentException("잔액이 부족합니다. 현재 잔액: " + balance.getHwanseungPay() + "원");
        }

        try {
            balance.setHwanseungPay(balance.getHwanseungPay() - useVO.getAmount());
            payBalanceRepository.save(balance);

            // 4. 영수증(History) 기록
            PayHistory history = PayHistory.builder().userId(Long.valueOf(userId)).impUid("INTERNAL_PAY").merchantUid(useVO.getMerchantUid()).type("USE").amount(useVO.getAmount()).build();
            payHistoryRepository.save(history);

            return true;
        } catch (Exception e) {
            System.err.println("포인트 차감 중 서버 에러: " + e.getMessage());
            throw new RuntimeException("DB 처리 실패로 결제가 취소되었습니다.");
        }
    }

    @Transactional(readOnly = true)
    public int getBalance(Long userId) {
        return payBalanceRepository.findByUserId(userId)
                .map(PayBalance::getHwanseungPay)
                .orElse(0); // 통장이 없으면 0원
    }
}