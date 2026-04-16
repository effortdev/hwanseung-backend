package com.hwanseung.backend.domain.user.service;

import jakarta.transaction.Transactional;
import com.hwanseung.backend.domain.user.config.CustomUserDetails;
import com.hwanseung.backend.domain.user.config.JwtTokenProvider;
import com.hwanseung.backend.domain.user.entity.Auth;
import com.hwanseung.backend.domain.user.entity.Role;
import com.hwanseung.backend.domain.user.entity.User;
import com.hwanseung.backend.domain.admin.dto.Status;
import com.hwanseung.backend.domain.user.dto.AuthRequestDTO;
import com.hwanseung.backend.domain.user.dto.AuthResponseDTO;
import com.hwanseung.backend.domain.user.dto.UserRequestDTO;
import com.hwanseung.backend.domain.user.repository.AuthRepository;
import com.hwanseung.backend.domain.user.repository.UserRepository;

import com.hwanseung.backend.domain.user.dto.PayBalance;
import com.hwanseung.backend.domain.user.controller.PayBalanceRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final VerificationService verificationService;
    private final MailService mailService;
    private final SmsService smsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    @Value("${google.client.id}")
    private String googleClientId;
    public void requestEmailVerification(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }
        String code = verificationService.createCode(email);
        mailService.sendEmail(email, code);
    }

    public void requestSmsVerification(String phoneNumber) {
        if (userRepository.existsByContact(phoneNumber)) {
            throw new IllegalStateException("이미 가입된 연락처입니다.");
        }
        String code = verificationService.createCode(phoneNumber);
        smsService.sendSms(phoneNumber, code);
    }

    public boolean checkVerification(String key, String code) {
        return verificationService.verify(key, code);
    }

    private final PayBalanceRepository payBalanceRepository;
    @Transactional
    public AuthResponseDTO login(AuthRequestDTO requestDto) {
        System.out.print("requestDto");
        System.out.println(requestDto);
        User user = this.userRepository.findByUsername(requestDto.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."));

        if (user.getStatus() == com.hwanseung.backend.domain.admin.dto.Status.PENDING) {
            throw new IllegalArgumentException("추가 정보 입력(연락처 인증)이 완료되지 않은 계정입니다.");
        }

        if (user.getStatus() == null || !"ACTIVE".equals(user.getStatus().name())) {
            throw new IllegalArgumentException("탈퇴하거나 정지된 계정입니다.");
        }

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }


        String accessToken = this.jwtTokenProvider.generateAccessToken(
                new UsernamePasswordAuthenticationToken(new CustomUserDetails(user), user.getPassword()));
        String refreshToken = this.jwtTokenProvider.generateRefreshToken(
                new UsernamePasswordAuthenticationToken(new CustomUserDetails(user), user.getPassword()));

        if (this.authRepository.existsByUser(user)) {
            user.getAuth().setAccessToken(accessToken);
            user.getAuth().setRefreshToken(refreshToken);
            return new AuthResponseDTO(user.getAuth());
        }

        Auth auth = this.authRepository.save(Auth.builder()
                .user(user)
                .tokenType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());
        return new AuthResponseDTO(auth);
    }

    @Transactional
    public void signup(UserRequestDTO requestDto) {
        requestDto.setRole(Role.ROLE_USER);
        requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));//암호화가 이루어지는 곳
        User savedUser = this.userRepository.save(requestDto.toEntity());
        PayBalance newBalance = new PayBalance();
        newBalance.setUserId(String.valueOf(savedUser.getId()));
        newBalance.setHwanseungPay(0);

        this.payBalanceRepository.save(newBalance);
    }

    @Transactional
    public String refreshToken(String refreshToken) {
        if (this.jwtTokenProvider.validateToken(refreshToken)) {
            Auth auth = this.authRepository.findByRefreshToken(refreshToken).orElseThrow(
                    () -> new IllegalArgumentException("해당 REFRESH_TOKEN 을 찾을 수 없습니다.\nREFRESH_TOKEN = " + refreshToken));

            String newAccessToken = this.jwtTokenProvider.generateAccessToken(
                    new UsernamePasswordAuthenticationToken(
                            new CustomUserDetails(auth.getUser()), auth.getUser().getPassword()));
            auth.setAccessToken(newAccessToken);
            return newAccessToken;
        }

        return null;
    }

    @Transactional
    public AuthResponseDTO googleLogin(String idTokenString) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken == null) throw new IllegalArgumentException("유효하지 않은 구글 토큰입니다.");

        Payload payload = idToken.getPayload();
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String googleId = payload.getSubject();

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .username("google_" + googleId.substring(0, 10))
                            .name(name)
                            .nickname("구글사용자_" + googleId.substring(0, 5))
                            .email(email)
                            .password(passwordEncoder.encode("SOCIAL_AUTH_PWD_" + googleId))
                            .provider("GOOGLE")
                            .providerId(googleId)
                            .role(Role.ROLE_USER)
                            .status(Status.PENDING)
                            .build();

                    User savedUser = userRepository.save(newUser);

                    PayBalance newBalance = new PayBalance();
                    newBalance.setUserId(String.valueOf(savedUser.getId()));
                    newBalance.setHwanseungPay(0);
                    payBalanceRepository.save(newBalance);

                    return savedUser;
                });

        String accessToken = jwtTokenProvider.generateAccessToken(
                new UsernamePasswordAuthenticationToken(new CustomUserDetails(user), user.getPassword()));
        String refreshToken = jwtTokenProvider.generateRefreshToken(
                new UsernamePasswordAuthenticationToken(new CustomUserDetails(user), user.getPassword()));

        Auth auth = authRepository.findByUser(user)
                .map(existingAuth -> {
                    existingAuth.setAccessToken(accessToken);
                    existingAuth.setRefreshToken(refreshToken);
                    return authRepository.save(existingAuth);
                })
                .orElseGet(() -> authRepository.save(Auth.builder()
                        .user(user)
                        .tokenType("Bearer")
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build()));

        return new AuthResponseDTO(auth);
    }


}