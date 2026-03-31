package com.hwanseung.backend.domain.user.controller;

import com.hwanseung.backend.domain.user.vo.PayBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayBalanceRepository extends JpaRepository<PayBalance, String> {
    // 🌟 주의: PayBalance의 기본키(@Id)가 String 타입인 userId이므로 두 번째 칸에 String을 적습니다.
}