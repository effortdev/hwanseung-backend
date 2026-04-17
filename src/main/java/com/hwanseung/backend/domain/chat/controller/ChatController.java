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

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);

        try {
            String receiverId = message.getReceiverId();

            System.out.println("🔥 [탐지기 2번] 백엔드가 인식한 receiverId: " + receiverId);

            if (receiverId != null) {
                Notification chatNoti = Notification.builder()
                        .receiverId(receiverId)
                        .content(message.getContent())
                        .type("CHAT")
                        .relatedStringId(message.getRoomId())
                        .build();


                messagingTemplate.convertAndSend("/sub/user/" + receiverId + "/notification", chatNoti);
            }
        } catch (Exception e) {
            System.err.println("채팅 알림 발송 실패: " + e.getMessage());
        }
    }
}
