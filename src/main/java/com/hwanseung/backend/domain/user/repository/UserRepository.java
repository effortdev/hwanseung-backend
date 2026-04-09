package com.hwanseung.backend.domain.user.repository;

import com.hwanseung.backend.domain.admin.dto.Status;
import com.hwanseung.backend.domain.user.entity.User;
import com.hwanseung.backend.domain.user.dto.UserRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByName(String name);
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String Nickname);

    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);

    long count();
    long countByCreatedAtAfter(LocalDateTime dateTime);
    long countByStatus(Status status);

    // 검색어(이메일 또는 닉네임)를 포함한 페이징 조회
    Page<User> findByEmailContainingOrNicknameContaining(String email, String nickname, Pageable pageable);

    @Modifying
    @Query("UPDATE User u SET u.nickname = :#{#dto.nickname}, u.email = :#{#dto.email}, " +
            "u.contact = :#{#dto.contact}, u.address = :#{#dto.address}, " +
            "u.detailAddress = :#{#dto.detailAddress}, u.zipCode = :#{#dto.zipCode} " +
            " WHERE u.id = :#{#dto.id}")
    int updateUser(@Param("dto") UserRequestDTO dto);

    @Query("SELECT u FROM User u WHERE u.status = 'SUSPENDED' " +
            "AND (:keyword = '' OR u.nickname LIKE %:keyword% OR u.email LIKE %:keyword%)")
    Page<User> findSuspendedUsers(@Param("keyword") String keyword, Pageable pageable);
}
