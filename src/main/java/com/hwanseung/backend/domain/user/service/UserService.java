package com.hwanseung.backend.domain.user.service;

import jakarta.transaction.Transactional;
import com.hwanseung.backend.domain.user.entity.User;
import com.hwanseung.backend.domain.user.dto.UserRequestDTO;
import com.hwanseung.backend.domain.user.dto.UserResponseDTO;
import com.hwanseung.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /** User 조회 */
    @Transactional
    public UserResponseDTO findById(Long id) {
        User user = this.userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. user_id = " + id));
        return new UserResponseDTO(user);
    }

    /** User 수정 (🌟 마법의 더티 체킹 적용) */
    @Transactional
    public void update(Long id, UserRequestDTO requestDto) {
        // 1. DB에서 수정할 유저를 찾아옵니다.
        User user = this.userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. user_id = " + id));

        // 2. 찾아온 유저의 정보를 프론트에서 받아온 DTO 정보로 덮어씌웁니다.
        // (@Transactional 덕분에 메서드가 끝날 때 스프링이 알아서 DB에 UPDATE 쿼리를 쏩니다!)
        user.setNickname(requestDto.getNickname());
        user.setEmail(requestDto.getEmail());
        user.setContact(requestDto.getContact());
        user.setAddress(requestDto.getAddress());
        user.setDetailAddress(requestDto.getDetailAddress());
        user.setZipCode(requestDto.getZipCode());
    }
    /** User 삭제 */
    @Transactional
    public void delete(Long id) {
        User user = this.userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. user_id = " + id));
        this.userRepository.delete(user);
    }

    public boolean isUseridDuplicate(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean isNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public boolean isEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }
}