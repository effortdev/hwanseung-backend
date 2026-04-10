package com.hwanseung.backend.domain.admin.service;

import com.hwanseung.backend.domain.admin.dto.Status;
import com.hwanseung.backend.domain.admin.dto.UserResponseDto;
import com.hwanseung.backend.domain.user.dto.UserResponseDTO;
import com.hwanseung.backend.domain.user.entity.User;
import com.hwanseung.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;

    // 2번: 사용자 관리 - 페이징 및 검색
    public Page<UserResponseDto> getUserList(String keyword, Pageable pageable) {
        System.out.println("pageNumber: %d, sort : %s".formatted(pageable.getPageNumber(), pageable.getSort()));
        Page<User> users;
        if (keyword == null || keyword.isBlank()) {
            users = userRepository.findAll(pageable);
            System.out.println("555555");
            System.out.println(users);
        } else {
            users = userRepository.findByEmailContainingOrNicknameContaining(keyword, keyword, pageable);
            System.out.println("66666");
            System.out.println(users);
        }

        return users.map(u -> new UserResponseDto(
                u.getId(), u.getUsername(), u.getNickname(),
                u.getTrustScore(), u.getReportCount(), u.getStatus()
        ));
    }

    // 2번: 사용자 관리 - 상태 변경 (Soft Delete/Suspension)
    @Transactional
    public void updateUserStatus(Long id, String status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        user.setStatus( Status.valueOf(status)); // 유저 엔티티 내 상태 업데이트 로직
    }
}
