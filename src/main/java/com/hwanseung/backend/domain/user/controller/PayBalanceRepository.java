package com.hwanseung.backend.domain.user.controller;

import com.hwanseung.backend.domain.user.dto.PayBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayBalanceRepository extends JpaRepository<PayBalance, String> {
}