package com.hwanseung.backend.domain.chat.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwanseung.backend.domain.chat.entity.ChatMessage;
import com.hwanseung.backend.domain.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;

    private final ChatMessageRepository chatMessageRepository;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());

            JsonNode jsonNode = objectMapper.readTree(publishMessage);
            String roomId = jsonNode.get("roomId").asText();
            String sender = jsonNode.get("sender").asText();
            String content = jsonNode.get("content").asText();

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setRoomId(roomId);
            chatMessage.setSenderId(sender);
            chatMessage.setContent(content);

            redisTemplate.opsForList().rightPush("chat_messages_buffer", publishMessage);

            messagingTemplate.convertAndSend("/sub/chat/room/" + roomId, publishMessage);

        } catch (Exception e) {
            log.error("Redis 메시지 파싱 및 DB 저장 중 에러 발생: {}", e.getMessage());
        }
    }
}