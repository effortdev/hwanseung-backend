package com.hwanseung.backend.domain.admin.repository;

import com.hwanseung.backend.domain.admin.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT COALESCE(SUM(t.price), 0) FROM Transaction t")
    long sumTotalPrice();

    // After 대신 GreaterThanEqual을 사용하여 00시 정각 데이터 유실 방지
    long countByCreatedAtGreaterThanEqual(LocalDateTime start);
}
