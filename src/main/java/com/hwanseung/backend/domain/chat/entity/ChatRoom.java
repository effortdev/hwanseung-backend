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
@EntityListeners(AuditingEntityListener.class) // 생성 시간 자동 기록을 위해 필요
public class ChatRoom {

    @Id
    @Column(name = "room_id", updatable = false)
    private String roomId; // UUID 형태의 랜덤 방 번호 (예: 123e4567-e89b-...)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType; // TRADE (중고거래) or ADMIN (관리자 문의)

    // 어떤 상품에 대한 채팅인지 (관리자 문의일 때는 null 허용)
    private Long itemId;

    // 로그인한 유저의 ID 타입이 Long인지 String인지에 맞춰서 변경하세요! (여기서는 임시로 String 사용)
    @Column(nullable = false)
    private String buyerId; // 구매자 (또는 문의하는 일반 유저)

    @Column(nullable = false)
    private String sellerId; // 판매자 (또는 관리자)

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt; // 방 생성 시간

    // 채팅방을 처음 만들 때 사용하는 팩토리 메서드
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