package com.hwanseung.backend.domain.user.service;

// 삭제: import jakarta.transaction.Transactional;
import org.springframework.transaction.annotation.Transactional; // 이걸로 교체
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

    /** User 수정 */
    @Transactional
    public void update(Long id, UserRequestDTO requestDto) {
        User user = this.userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. user_id = " + id));
        this.userRepository.updateUser(requestDto);
    }

    /** User 삭제 */
    @Transactional
    public void delete(Long id) {
        User user = this.userRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다. user_id = " + id));
        this.userRepository.delete(user);
    }

    /* User테이블 총 사용자 수 */
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
    }
}