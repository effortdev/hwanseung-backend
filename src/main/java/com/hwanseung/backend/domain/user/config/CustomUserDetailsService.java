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
        // 🌟 다시 findByUsername 으로 원상복구!
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("해당 아이디를 가진 유저가 존재하지 않습니다. userid = " + username));
        return new CustomUserDetails(user);
    }

    public UserDetails loadUserByUserId(String userid) {
        // 🌟 findByid(오타) -> findByUserid 로 변경!
        User user = userRepository.findByUsername(userid)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음: " + userid));
        return new CustomUserDetails(user);
    }

}