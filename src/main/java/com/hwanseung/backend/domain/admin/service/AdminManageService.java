package com.hwanseung.backend.domain.admin.service;

import com.hwanseung.backend.domain.admin.dto.AdminCreateRequestDto;
import com.hwanseung.backend.domain.admin.dto.AdminRoleUpdateDto;
import com.hwanseung.backend.domain.admin.dto.AdminUserResponseDto;
import com.hwanseung.backend.domain.admin.dto.Status;
import com.hwanseung.backend.domain.user.entity.Role;
import com.hwanseung.backend.domain.user.entity.User;
import com.hwanseung.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminManageService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<AdminUserResponseDto> getAdminList() {
        List<Role> adminRoles = Arrays.asList(Role.ROLE_SUPER, Role.ROLE_ADMIN, Role.ROLE_SUB);
        List<User> admins = userRepository.findByRoleIn(adminRoles);
        return admins.stream()
                .map(AdminUserResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateRole(AdminRoleUpdateDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. id=" + dto.getUserId()));

        Role newRole = dto.getRole();
        if (newRole == Role.ROLE_USER) {
            throw new IllegalArgumentException("관리자 관리에서 일반 사용자 권한으로 변경할 수 없습니다.");
        }

        user.setRole(newRole);
        userRepository.save(user);
    }

    @Transactional
    public void createAdmin(AdminCreateRequestDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        if (userRepository.existsByNickname(dto.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 별명입니다.");
        }

        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        Random random = new Random();

        String randomEmail = "admin_" + uuid + "@hwanseung.com";
        String randomContact = "010-0000-" + String.format("%04d", random.nextInt(10000));
        String randomBirthday = "1990-01-01";
        String randomGender = "N/A";

        if (userRepository.existsByEmail(randomEmail)) {
            randomEmail = "admin_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12) + "@hwanseung.com";
        }

        User newAdmin = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .nickname(dto.getNickname())
                .role(dto.getRole())
                .email(randomEmail)
                .contact(randomContact)
                .birthday(randomBirthday)
                .gender(randomGender)
                .zipCode("00000")
                .address("관리자 생성 계정")
                .detailAddress("-")
                .status(Status.ACTIVE)
                .provider("LOCAL")
                .trustScore(0)
                .reportCount(0)
                .build();

        userRepository.save(newAdmin);
    }
}
