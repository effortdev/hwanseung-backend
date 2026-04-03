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

    @Transactional // 🌟 매우 중요: 영수증 저장과 잔액 업데이트는 "한 몸"입니다.
    public boolean chargeHwanseungPay(PayHistory history) {
        try {
            // 1. 영수증(History) 저장
            // 💡 팁: history 객체에는 이미 Controller에서 넣어준 userId, amount 등이 담겨 있습니다.
            payHistoryRepository.save(history);

            // 2. 해당 유저의 통장(Balance) 찾기
            PayBalance balance = payBalanceRepository.findById(history.getUserId())
                    .orElseGet(() -> {
                        // 통장이 없는 신규 유저라면 새로 생성
                        PayBalance newBalance = new PayBalance();
                        newBalance.setUserId(history.getUserId());
                        newBalance.setHwanseungPay(0);
                        return newBalance;
                    });

            // 3. 잔액 업데이트
            int currentPay = balance.getHwanseungPay();
            balance.setHwanseungPay(currentPay + history.getAmount());

            // 4. 저장 (JPA의 더티 체킹 기능 덕분에 사실 save를 안 써도 되지만, 명시적으로 적어줍니다.)
            payBalanceRepository.save(balance);

            return true;
        } catch (Exception e) {
            // 🌟 실무에서는 System.out 대신 로그 라이브러리를 쓰지만, 현재는 에러 파악용으로 출력
            System.err.println("결제 처리 중 서버 에러: " + e.getMessage());
            // @Transactional이 달려있으므로, 여기서 RuntimeException을 던지면 자동으로 롤백됩니다.
            throw new RuntimeException("DB 저장 실패로 인한 결제 취소");
        }
    }
    @Transactional
    public boolean useHwanseungPay(String userId, PayUseVO useVO) {
        // 1. 내 통장(Balance) 찾기
        PayBalance balance = payBalanceRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("지갑 정보가 존재하지 않습니다."));

        // 🌟 2. 잔액 부족 검사 (가장 중요!)
        if (balance.getHwanseungPay() < useVO.getAmount()) {
            throw new IllegalArgumentException("잔액이 부족합니다. 현재 잔액: " + balance.getHwanseungPay() + "원");
        }

        try {
            // 3. 잔액 차감 (현재 돈 - 결제할 돈)
            balance.setHwanseungPay(balance.getHwanseungPay() - useVO.getAmount());
            payBalanceRepository.save(balance);

            // 4. 영수증(History) 기록
            PayHistory history = new PayHistory();
            history.setUserId(userId);
            history.setImpUid("INTERNAL_PAY"); // 내부 결제이므로 포트원 고유번호 대신 자체 표기
            history.setMerchantUid(useVO.getMerchantUid()); // 프론트에서 넘어온 주문번호
            history.setType("USE"); // 충전은 CHARGE, 사용은 USE 로 명확히 구분
            history.setAmount(useVO.getAmount()); // 차감된 금액 기록

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