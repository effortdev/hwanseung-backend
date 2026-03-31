package com.hwanseung.backend.domain.user.config;

import com.hwanseung.backend.domain.user.entity.User;
import com.hwanseung.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 🚩 전달받은 'username'(로그인 시 입력한 아이디)을 'userid' 필드에서 찾아야 합니다.
        System.out.println("CustomUserDetailsService loadUserByUsername 실행 (로그인 아이디: " + username + ")");

        // findByUsername -> findByUserid 로 변경
        User user = userRepository.findByUserid(username).orElseThrow(
                () -> new UsernameNotFoundException("해당 아이디를 가진 유저가 존재하지 않습니다. userid = " + username));

        return new CustomUserDetails(user);
    }

    // 필요시 추가
    public UserDetails loadUserByUserId(Long userId) throws IllegalArgumentException {
        System.out.println("CustomUserDetailsService loadUserByUserId 실행");
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("해당 유저가 존재하지 않습니다. user_id = " + userId));
        return new CustomUserDetails(user);
    }

    // 필요시 추가
    public UserDetails loadUserByEmail(String email) throws IllegalArgumentException {
        System.out.println("CustomUserDetailsService loadUserByEmail 실행");
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("해당 유저가 존재하지 않습니다. email = " + email));
        return new CustomUserDetails(user);
    }
}