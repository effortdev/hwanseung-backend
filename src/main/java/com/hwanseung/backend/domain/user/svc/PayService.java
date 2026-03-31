package com.hwanseung.backend.domain.user.svc;


import com.hwanseung.backend.domain.user.controller.PayBalanceRepository;
import com.hwanseung.backend.domain.user.controller.PayHistoryRepository;
import com.hwanseung.backend.domain.user.vo.PayBalance;
import com.hwanseung.backend.domain.user.vo.PayHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service // "나는 비즈니스 로직을 처리하는 매니저다!"
public class PayService {

    @Autowired
    private PayHistoryRepository payHistoryRepository; // 장부(영수증) 담당 로봇

    @Autowired
    private PayBalanceRepository payBalanceRepository; // 🌟 추가: 통장(잔액) 담당 로봇

    @Transactional // 마법진: 아래 작업 중 하나라도 에러가 나면 전부 원래대로 되돌림(Rollback)!
    public boolean chargeHwanseungPay(PayHistory history) {
        try {
            // 1. 가장 먼저 영수증(PayHistory)을 DB에 저장합니다. (INSERT)
            payHistoryRepository.save(history);

            // 2. 결제한 손님의 통장(PayBalance)을 DB에서 찾아옵니다.
            // 💡 실무 꿀팁: 만약 생전 처음 충전하는 손님이라 DB에 통장이 없다면?
            // 에러를 내지 말고, 잔액이 0원인 새 통장을 그 자리에서 발급해 줍니다! (orElseGet 사용)
            PayBalance balance = payBalanceRepository.findById(history.getUserId())
                    .orElseGet(() -> {
                        PayBalance newBalance = new PayBalance();
                        newBalance.setUserId(history.getUserId());
                        newBalance.setHwanseungPay(0); // 첫 발급이니 잔액은 0원
                        return newBalance;
                    });

            // 3. 기존 잔액에 이번에 충전한 금액(amount)을 더해줍니다.
            int newTotalPay = balance.getHwanseungPay() + history.getAmount();
            balance.setHwanseungPay(newTotalPay);

            // 4. 잔고가 업데이트된 통장을 다시 DB 금고에 넣습니다. (UPDATE 또는 INSERT)
            payBalanceRepository.save(balance);

            System.out.println("✅ [Service] 장부 기록 및 잔액 충전 완벽 성공! 현재 잔액: " + newTotalPay + "원");
            return true;

        } catch (Exception e) {
            System.out.println("❌ DB 저장 에러: " + e.getMessage());
            return false;
        }
    }
}