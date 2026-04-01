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

    // ✅ 1. DB 저장을 위해 Repository 추가
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // 2. Redis에서 온 메시지를 꺼내서 문자열(JSON)로 변환
            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());

            // 3. JSON 문자열을 읽어서 필요한 데이터(방번호, 보낸사람, 내용) 추출
            JsonNode jsonNode = objectMapper.readTree(publishMessage);
            String roomId = jsonNode.get("roomId").asText();
            String sender = jsonNode.get("sender").asText();
            String content = jsonNode.get("content").asText();

            // 4. 💾 추출한 데이터를 바탕으로 DB에 저장할 엔티티 조립
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setRoomId(roomId);
            chatMessage.setSenderId(sender); // 프론트의 'sender'를 DB의 'senderId' 컬럼에 매핑
            chatMessage.setContent(content);

            redisTemplate.opsForList().rightPush("chat_messages_buffer", publishMessage);

            // 6. DB 저장이 무사히 끝났다면, 기존처럼 채팅방에 접속 중인 사람들에게 메시지 쏴주기
            messagingTemplate.convertAndSend("/sub/chat/room/" + roomId, publishMessage);

        } catch (Exception e) {
            log.error("Redis 메시지 파싱 및 DB 저장 중 에러 발생: {}", e.getMessage());
        }
    }
}