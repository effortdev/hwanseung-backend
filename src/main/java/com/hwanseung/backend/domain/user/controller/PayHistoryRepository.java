package com.hwanseung.backend.domain.user.controller;

import com.hwanseung.backend.domain.user.dto.PayHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayHistoryRepository extends JpaRepository<PayHistory, Long> {
}