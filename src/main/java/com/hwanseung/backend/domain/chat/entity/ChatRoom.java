package com.hwanseung.backend.domain.chat.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ChatRoom {

    @Id
    @Column(name = "room_id", updatable = false)
    private String roomId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType;

    private Long itemId;

    @Column(nullable = false)
    private String buyerId;

    @Column(nullable = false)
    private String sellerId;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public static ChatRoom create(RoomType type, Long itemId, String buyerId, String sellerId) {
        ChatRoom room = new ChatRoom();
        room.roomId = UUID.randomUUID().toString();
        room.roomType = type;
        room.itemId = itemId;
        room.buyerId = buyerId;
        room.sellerId = sellerId;
        return room;
    }
}