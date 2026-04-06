package com.hwanseung.backend.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    public void sendEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[환승마켓] 회원가입 이메일 인증번호");
        message.setText("안녕하세요. 요청하신 인증번호는 [" + code + "] 입니다.");
        mailSender.send(message);
    }
}