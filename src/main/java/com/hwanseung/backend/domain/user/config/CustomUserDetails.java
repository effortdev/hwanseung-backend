package com.hwanseung.backend.domain.user.config;

import com.hwanseung.backend.domain.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


/*
Spring Security 에서 인증에 사용하기 위한 User 정보를 담은 객체.
public interface UserDetails를 implements하여 CustomUserDetails로 커스텀.

SpringBoot WebSecurity에서 JWT(JSON Web Token)의 데이터 흐름:
CustomUserDetails -> CustomUserDetailsService ->
JwtTokenProvider -> JwtTokenFilter -> WebSecurityConfig
*/
//User.java에 Authority가 추가된 CustomUser Class임
public class CustomUserDetails implements UserDetails {
    private final User user;

    // Constructor
    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()));
    }

    public Long getId() {
        return user.getId();
    }

    public String getNickname() {
        return user.getNickname();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getContact() {
        return user.getContact();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}