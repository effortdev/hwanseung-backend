package com.hwanseung.backend.domain.user.controller;

import com.hwanseung.backend.domain.user.dto.PayHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PayHistoryRepository extends JpaRepository<PayHistory, Long> {

    @Query("SELECT SUM(p.amount) FROM PayHistory p WHERE p.username = :userId")
    Integer sumAmountByUsername(@Param("userId") String userId);
}