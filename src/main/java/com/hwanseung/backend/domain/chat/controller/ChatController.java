package com.hwanseung.backend.domain.chat.controller;

import com.hwanseung.backend.domain.chat.dto.ChatMessage;
import com.hwanseung.backend.domain.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic = new ChannelTopic("chatroom");

    // 🚀 [알림 추가 1] 실시간 알림을 특정 유저에게 쏴주기 위한 템플릿 주입
    private final SimpMessagingTemplate messagingTemplate;

    // 클라이언트가 "/pub/chat/message"로 메시지를 보내면 이 메서드가 실행됨
    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        // 메시지를 Redis Topic으로 발행 (서버가 여러 대여도 Redis를 통해 공유됨)
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);

        // ==========================================
        // 🚀 2. (추가) 상대방의 "개인 알림 채널"로 즉각 STOMP 발사! (플로팅 아이콘 뱃지용)
        // ==========================================
        try {
            // 💡 주의: ChatMessage DTO 안에 상대방의 ID를 알 수 있는 수신자(receiverId) 정보가 있어야 합니다!
            // 만약 DTO에 없다면, DB에서 roomId로 조회해오는 로직이 한 줄 필요할 수 있습니다.
            String receiverId = message.getReceiverId(); // 상대방 아이디 가져오기 (메서드명은 DTO에 맞게 수정!)

            System.out.println("🔥 [탐지기 2번] 백엔드가 인식한 receiverId: " + receiverId);

            if (receiverId != null) {
                Notification chatNoti = Notification.builder()
                        .receiverId(receiverId)
                        .content(message.getContent())
                        .type("CHAT") // 프론트엔드 교통정리용 핵심 키워드!
//                        .relatedItemId(Long.valueOf(message.getRoomId()))
                        // 🚀 2. 새로 만든 문자열 전용 필드에 방 번호 그대로 넣기!
                        .relatedStringId(message.getRoomId())
                        .build();

                // (선택) 알림 내역을 DB에도 남기고 싶다면 주석 해제
                // notificationRepository.save(chatNoti);

                // 🚀 상대방이 어디에 있든 헤더(또는 플로팅 아이콘)로 STOMP 알림 즉시 전송!
                messagingTemplate.convertAndSend("/sub/user/" + receiverId + "/notification", chatNoti);
            }
        } catch (Exception e) {
            // 알림 발송 중 에러가 나도 메인 채팅 전송(Redis)에는 영향을 주지 않도록 보호!
            System.err.println("채팅 알림 발송 실패: " + e.getMessage());
        }
    }
}
