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
        // 🌟 해결: 입구에서 받은 'username'을 그대로 사용합니다.
        // (참고: 스프링 시큐리티 표준 규격상 매개변수명이 username일 뿐, 내용은 우리가 만든 'userid'가 들어옵니다.)
        System.out.println("CustomUserDetailsService loadUserByUsername 실행 (로그인 아이디: " + username + ")");

        User user = userRepository.findByUserid(username) // 👈 username으로 바꿔주세요!
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다: " + username));

        return new CustomUserDetails(user);
    }

    // 🌟 이 메서드도 사용하는 곳이 있다면 이름을 맞춰줍니다.
    public UserDetails loadUserByUserId(String userid) {
        User user = userRepository.findByUserid(userid)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음: " + userid));
        return new CustomUserDetails(user);
    }

    public UserDetails loadUserByEmail(String email) throws IllegalArgumentException {
        System.out.println("CustomUserDetailsService loadUserByEmail 실행");
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException("해당 유저가 존재하지 않습니다. email = " + email));
        return new CustomUserDetails(user);
    }
}