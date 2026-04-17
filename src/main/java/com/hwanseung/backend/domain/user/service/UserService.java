package com.hwanseung.backend.domain.user.service;

import com.hwanseung.backend.domain.user.config.CustomUserDetails;
import com.hwanseung.backend.domain.user.config.JwtTokenProvider;
import com.hwanseung.backend.domain.user.entity.Auth;
import com.hwanseung.backend.domain.user.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional; // 이걸로 교체
import com.hwanseung.backend.domain.user.entity.User;
import com.hwanseung.backend.domain.user.dto.UserRequestDTO;
import com.hwanseung.backend.domain.user.dto.UserResponseDTO;
import com.hwanseung.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import com.hwanseung.backend.domain.admin.dto.Status;
import com.hwanseung.backend.domain.user.entity.Role;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${custom.upload-path}")
    private String uploadPath;

    @Transactional
    public UserResponseDTO findById(Long id) {
        User user = this.userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. user_id = " + id));
        return new UserResponseDTO(user);
    }

    @Transactional
    public void update(Long id, UserRequestDTO requestDto, MultipartFile profileImage) throws IOException {
        User user = this.userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. user_id = " + id));

        if (profileImage != null && !profileImage.isEmpty()) {

            String oldImagePath = user.getProfileImagePath();
            if (oldImagePath != null && !oldImagePath.isEmpty()) {
                String relativePath = oldImagePath.replace("/api/imgs/", "");
                File oldFile = new File(uploadPath, relativePath);

                if (oldFile.exists()) {
                    if (oldFile.delete()) {
                        System.out.println("기존 프로필 파일 삭제 성공: " + oldFile.getPath());
                    } else {
                        System.out.println("기존 프로필 파일 삭제 실패: " + oldFile.getPath());
                    }
                }
            }

            String originalFileName = profileImage.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String savedFileName = uuid + "_" + originalFileName;

            File profileDir = new File(uploadPath, "profile");
            if (!profileDir.exists()) {
                profileDir.mkdirs();
            }

            File saveFile = new File(profileDir, savedFileName);
            profileImage.transferTo(saveFile);

            user.setProfileImagePath("/api/imgs/profile/" + savedFileName);
            user.setProfileOriginalName(originalFileName);
        }

        user.setNickname(requestDto.getNickname());
        user.setEmail(requestDto.getEmail());
        user.setContact(requestDto.getContact());
        user.setAddress(requestDto.getAddress());
        user.setDetailAddress(requestDto.getDetailAddress());
        user.setZipCode(requestDto.getZipCode());
        user.setGender(requestDto.getGender());
        user.setBirthday(requestDto.getBirthday());
        if (requestDto.getPassword() != null && !requestDto.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        }
        if (requestDto.getNeighborhood() != null) {
            user.setNeighborhood(requestDto.getNeighborhood());
            user.setNeighborhoodAuthenticated(true);
        }

    }
    @Transactional
    public void withdraw(Long userId, String Password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(Password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        authRepository.deleteByUserId(userId);
        user.withdraw();

    }

    @Transactional
    public User completeSocialSignup(String username, String contact) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        user.setContact(contact);
        user.setStatus(Status.ACTIVE);
        userRepository.save(user);

        return user;
    }

    @Transactional
    public void updateAuthToken(User user, String newAccessToken) {
        Auth auth = authRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("인증 정보를 찾을 수 없습니다."));

        auth.setAccessToken(newAccessToken);

        authRepository.save(auth);
    }


    @Transactional(readOnly = true)
    public long getTotalUserCount() {
        return userRepository.count();
    }

    public boolean isUseridDuplicate(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean isNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public boolean isEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);

    }public boolean isContactDuplicate(String contact) {
        return userRepository.existsByContact(contact);
    }
}