package com.hwanseung.backend.domain.user.controller;

import com.hwanseung.backend.domain.user.vo.PayHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayHistoryRepository extends JpaRepository<PayHistory, Long> {
    // 놀랍게도 이 인터페이스 하나만 만들면 INSERT, SELECT, UPDATE가 모두 자동으로 됩니다!
}