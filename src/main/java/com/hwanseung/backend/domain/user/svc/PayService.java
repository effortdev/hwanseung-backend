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

            PayBalance balance = payBalanceRepository.findById(history.getUserId())
                    .orElseGet(() -> {
                        PayBalance newBalance = new PayBalance();
                        newBalance.setUserId(history.getUserId());
                        newBalance.setHwanseungPay(0);
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
        PayBalance balance = payBalanceRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("지갑 정보가 존재하지 않습니다."));

        if (balance.getHwanseungPay() < useVO.getAmount()) {
            throw new IllegalArgumentException("잔액이 부족합니다. 현재 잔액: " + balance.getHwanseungPay() + "원");
        }

        try {
            balance.setHwanseungPay(balance.getHwanseungPay() - useVO.getAmount());
            payBalanceRepository.save(balance);

            PayHistory history = new PayHistory();
            history.setUserId(userId);
            history.setImpUid("INTERNAL_PAY");
            history.setMerchantUid(useVO.getMerchantUid());
            history.setType("USE");
            history.setAmount(useVO.getAmount());

            payHistoryRepository.save(history);

            return true;
        } catch (Exception e) {
            System.err.println("포인트 차감 중 서버 에러: " + e.getMessage());
            throw new RuntimeException("DB 처리 실패로 결제가 취소되었습니다.");
        }
    }

    @Transactional(readOnly = true)
    public int getBalance(String userId) {
        return payBalanceRepository.findById(userId)
                .map(PayBalance::getHwanseungPay)
                .orElse(0); // 통장이 없으면 0원
    }
}