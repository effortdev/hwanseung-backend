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

    /** User 조회 */
    @Transactional
    public UserResponseDTO findById(Long id) { // 🌟 Long 복구
        User user = this.userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. user_id = " + id));
        return new UserResponseDTO(user);
    }

    /** User 수정 */
    @Transactional
    public void update(Long id, UserRequestDTO requestDto) { // 🌟 Long 복구
        User user = this.userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. user_id = " + id));

        // 더티 체킹으로 값만 업데이트
        user.setUsername(requestDto.getUsername());
        user.setNickname(requestDto.getNickname());
        user.setContact(requestDto.getContact());
        user.setAddress(requestDto.getAddress());
    }

    /** User 삭제 */
    @Transactional
    public void delete(Long id) { // 🌟 Long 복구
        User user = this.userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. user_id = " + id));
        this.userRepository.delete(user);
    }

    public boolean isUseridDuplicate(String userid) {
        return userRepository.existsByUsername(userid);
    }
    public boolean isNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public boolean isEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }
}