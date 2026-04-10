package com.hwanseung.backend.domain.user.repository;

import com.hwanseung.backend.domain.user.entity.Auth;
import com.hwanseung.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    Boolean existsByUser(User user);
    Optional<Auth> findByRefreshToken(String refreshToken);

    @Modifying // 🌟 DB의 데이터를 변경/삭제할 때 반드시 필요
    @Transactional
    @Query("DELETE FROM Auth a WHERE a.user.id = :userId")
    void deleteByUserId(Long userId);

    Optional<Auth> findByUser(User user);
}
