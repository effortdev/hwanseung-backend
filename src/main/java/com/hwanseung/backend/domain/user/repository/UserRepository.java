package com.hwanseung.backend.domain.user.repository;

import com.hwanseung.backend.domain.user.entity.User;
import com.hwanseung.backend.domain.user.dto.UserRequestDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByName(String name);
    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);

    long count();

    @Modifying
    @Query("UPDATE User u SET u.nickname = :#{#dto.nickname}, u.email = :#{#dto.email}, " +
            "u.contact = :#{#dto.contact}, u.address = :#{#dto.address}, " +
            "u.detailAddress = :#{#dto.detailAddress}, u.zipCode = :#{#dto.zipCode} " +
            " WHERE u.id = :#{#dto.id}")
    int updateUser(@Param("dto") UserRequestDTO dto);
}
