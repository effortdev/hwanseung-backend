package com.hwanseung.backend.domain.user.repository;

import com.hwanseung.backend.domain.user.entity.Auth;
import com.hwanseung.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    Boolean existsByUser(User user);
    Optional<Auth> findByRefreshToken(String refreshToken);
}
