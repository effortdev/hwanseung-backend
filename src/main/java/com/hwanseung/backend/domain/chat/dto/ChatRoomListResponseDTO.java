package com.hwanseung.backend.domain.chat.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatRoomListResponseDTO {
    private String roomId;
    private String buyerId;
    private String sellerId;
    private String itemName;
    private String lastMessage;
    private int unreadCount;
}