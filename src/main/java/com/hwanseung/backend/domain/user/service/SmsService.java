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

    // application.properties에 설정한 환경 변수값을 가져옵니다.
    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecret;

    @Value("${coolsms.sender.number}")
    private String senderNumber;

    private DefaultMessageService messageService;

    /**
     * 의존성 주입이 완료된 후, CoolSMS SDK를 초기화합니다.
     */
    @PostConstruct
    public void init() {
        // SDK 초기화 (API Key, API Secret, API URL 순서)
        this.messageService = NurigoApp.INSTANCE.initialize(
                apiKey,
                apiSecret,
                "https://api.coolsms.co.kr"
        );
    }

    /**
     * 실제 SMS를 발송하는 메서드
     * @param to 수신자 번호 (예: 01012345678)
     * @param verificationCode 발송할 인증번호
     */
    public void sendSms(String to, String verificationCode) {
        // 1. 메시지 객체 생성 및 설정
        Message message = new Message();
        message.setFrom(senderNumber); // 환경변수에서 가져온 내 번호
        message.setTo(to);             // 상대방 번호
        message.setText("[환승마켓] 인증번호 [" + verificationCode + "]를 입력해주세요. (3분 이내)");

        try {
            // 2. 단건 메시지 발송 요청
            SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));

            // 3. 성공 로그 확인 (콘솔에서 확인 가능)
            System.out.println("### SMS 발송 성공 ###");
            System.out.println("수신번호: " + response.getTo());
            System.out.println("상태 메시지: " + response.getStatusMessage());

        } catch (Exception e) {
            // 발송 실패 시 예외 처리
            System.err.println("### SMS 발송 실패: " + e.getMessage());
            throw new RuntimeException("문자 발송 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
//        // 콘솔에서 답변하는 임시로직
//    public void sendSms(String phoneNumber, String code) {
//        String content = "[환승마켓] 인증번호는 [" + code + "] 입니다. 타인에게 노출하지 마세요.";
//        System.out.println("--- SMS 발송 로그 ---");
//        System.out.println("수신번호: " + phoneNumber);
//        System.out.println("내용: " + content);
//        System.out.println("--------------------");
//    }
}


