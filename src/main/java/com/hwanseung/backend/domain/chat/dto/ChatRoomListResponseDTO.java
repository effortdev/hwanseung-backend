package com.hwanseung.backend.domain.chat.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatRoomListResponseDTO {
    private String roomId;
    private String buyerId;
    private String sellerId;
    private String itemName;     // 🚨 프론트가 그토록 애타게 찾던 상품 이름!
    private String lastMessage;  // 마지막 대화 내용
    private int unreadCount;     // 안 읽은 메시지 수
}