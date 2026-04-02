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
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("해당 아이디를 가진 유저가 존재하지 않습니다. userid = " + username));

        return new CustomUserDetails(user);
    }

    // 🌟 이 메서드도 사용하는 곳이 있다면 이름을 맞춰줍니다.
    public UserDetails loadUserByUserId(String userid) {
        User user = userRepository.findByid(userid)
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