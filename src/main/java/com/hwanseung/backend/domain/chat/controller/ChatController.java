package com.hwanseung.backend.domain.chat.controller;

import com.hwanseung.backend.domain.chat.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic = new ChannelTopic("chatroom");

    // 클라이언트가 "/pub/chat/message"로 메시지를 보내면 이 메서드가 실행됨
    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        // 메시지를 Redis Topic으로 발행 (서버가 여러 대여도 Redis를 통해 공유됨)
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }
}
