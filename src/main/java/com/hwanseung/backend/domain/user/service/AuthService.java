package com.hwanseung.backend.domain.user.service;

import jakarta.transaction.Transactional;
import com.hwanseung.backend.domain.user.config.CustomUserDetails;
import com.hwanseung.backend.domain.user.config.JwtTokenProvider;
import com.hwanseung.backend.domain.user.entity.Auth;
import com.hwanseung.backend.domain.user.entity.Role;
import com.hwanseung.backend.domain.user.entity.User;
import com.hwanseung.backend.domain.user.dto.AuthRequestDTO;
import com.hwanseung.backend.domain.user.dto.AuthResponseDTO;
import com.hwanseung.backend.domain.user.dto.UserRequestDTO;
import com.hwanseung.backend.domain.user.repository.AuthRepository;
import com.hwanseung.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
// 🌟 1. 지갑 관련 클래스들을 불러옵니다. (경로는 회원님의 프로젝트에 맞게 확인해 주세요!)
import com.hwanseung.backend.domain.user.dto.PayBalance;
import com.hwanseung.backend.domain.user.controller.PayBalanceRepository;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    private final PayBalanceRepository payBalanceRepository;
    /** 로그인 */
    @Transactional
    public AuthResponseDTO login(AuthRequestDTO requestDto) {
        // CHECK USERNAME AND PASSWORD
        User user = this.userRepository.findByUsername(requestDto.getUserid()).orElseThrow(
                () -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다. username = " + requestDto.getUserid()));
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다. username = " + requestDto.getUserid());
        }
        System.out.println("user:: "+user);


        // GENERATE ACCESS_TOKEN AND REFRESH_TOKEN
        String accessToken = this.jwtTokenProvider.generateAccessToken(
                                        new UsernamePasswordAuthenticationToken(new CustomUserDetails(user), user.getPassword()));
        String refreshToken = this.jwtTokenProvider.generateRefreshToken(
                new UsernamePasswordAuthenticationToken(new CustomUserDetails(user), user.getPassword()));

        // CHECK IF AUTH ENTITY EXISTS, THEN UPDATE TOKEN
        if (this.authRepository.existsByUser(user)) {
            user.getAuth().setAccessToken(accessToken);
            user.getAuth().setRefreshToken(refreshToken);
            return new AuthResponseDTO(user.getAuth());
        }

        // IF NOT EXISTS AUTH ENTITY, SAVE AUTH ENTITY AND TOKEN
        Auth auth = this.authRepository.save(Auth.builder()
                .user(user)
                .tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());
        return new AuthResponseDTO(auth);
    }

    /** 회원가입 */
    @Transactional
    public void signup(UserRequestDTO requestDto) {
        // SAVE USER ENTITY
        requestDto.setRole(Role.ROLE_USER);
        requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));//암호화가 이루어지는 곳
        User savedUser = this.userRepository.save(requestDto.toEntity());

        PayBalance newBalance = new PayBalance();
        newBalance.setUserId(String.valueOf(savedUser.getId())); // 방금 생성된 유저의 고유번호(예: 1) 세팅
        newBalance.setHwanseungPay(0); // 잔액 0원 세팅

        this.userRepository.save(requestDto.toEntity());
    }

    /** Token 갱신 */
    @Transactional
    public String refreshToken(String refreshToken) {
        // CHECK IF REFRESH_TOKEN EXPIRATION AVAILABLE, UPDATE ACCESS_TOKEN AND RETURN
        if (this.jwtTokenProvider.validateToken(refreshToken)) {
            Auth auth = this.authRepository.findByRefreshToken(refreshToken).orElseThrow(
                    () -> new IllegalArgumentException("해당 REFRESH_TOKEN 을 찾을 수 없습니다.\nREFRESH_TOKEN = " + refreshToken));

            String newAccessToken = this.jwtTokenProvider.generateAccessToken(
                    new UsernamePasswordAuthenticationToken(
                            new CustomUserDetails(auth.getUser()), auth.getUser().getPassword()));
            auth.setAccessToken(newAccessToken);
            return newAccessToken;
        }

        // IF NOT AVAILABLE REFRESH_TOKEN EXPIRATION, REGENERATE ACCESS_TOKEN AND REFRESH_TOKEN
        // IN THIS CASE, USER HAVE TO LOGIN AGAIN, SO REGENERATE IS NOT APPROPRIATE
        return null;
    }
}