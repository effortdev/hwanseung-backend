package com.hwanseung.backend.domain.user.config;

import com.hwanseung.backend.domain.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()));
    }

    public Long getId() { return user.getId(); }
    public String getEmail() { return user.getEmail(); }
    public String getContact() { return user.getContact(); }

    @Override
    public String getUsername() {
        // 🌟 시큐리티 규격에 맞춰 무조건 로그인 아이디 반환
        return user.getUsername();
    }

    @Override
    public String getPassword() { return user.getPassword(); }
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}