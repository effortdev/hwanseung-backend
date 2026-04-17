package com.hwanseung.backend.domain.user.service;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class SmsService {

    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecret;

    @Value("${coolsms.sender.number}")
    private String senderNumber;

    private DefaultMessageService messageService;

    @PostConstruct
    public void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(
                apiKey,
                apiSecret,
                "https://api.coolsms.co.kr"
        );
    }

    public void sendSms(String to, String verificationCode) {
        Message message = new Message();
        message.setFrom(senderNumber);
        message.setTo(to);
        message.setText("[환승마켓] 인증번호 [" + verificationCode + "]를 입력해주세요. (3분 이내)");

        try {
            SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));

            System.out.println("### SMS 발송 성공 ###");
            System.out.println("수신번호: " + response.getTo());
            System.out.println("상태 메시지: " + response.getStatusMessage());

        } catch (Exception e) {
            System.err.println("### SMS 발송 실패: " + e.getMessage());
            throw new RuntimeException("문자 발송 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}


