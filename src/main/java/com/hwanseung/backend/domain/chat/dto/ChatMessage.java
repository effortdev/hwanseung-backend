package com.hwanseung.backend.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String roomId;   // 채팅방 ID
    private String sender;   // 보낸 사람
    private String content;  // 메시지 내용

    private String receiverId; // STOMP 알림 배달을 위한 임시 주소표
}
