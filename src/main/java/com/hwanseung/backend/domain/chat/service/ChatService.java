package com.hwanseung.backend.domain.chat.service;

import com.hwanseung.backend.domain.chat.entity.ChatMessage;
import com.hwanseung.backend.domain.chat.entity.ChatRoom;
import com.hwanseung.backend.domain.chat.entity.RoomType;
import com.hwanseung.backend.domain.chat.repository.ChatMessageRepository;
import com.hwanseung.backend.domain.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    // 1. 중고거래 채팅방 생성 또는 조회
    @Transactional
    public ChatRoom createOrGetTradeRoom(Long itemId, String buyerId, String sellerId) {
        // 이미 방이 있는지 DB에서 확인
        return chatRoomRepository.findByRoomTypeAndItemIdAndBuyerIdAndSellerId(
                RoomType.TRADE, itemId, buyerId, sellerId
        ).orElseGet(() -> {
            // 방이 없으면 새로 생성해서 저장
            ChatRoom newRoom = ChatRoom.create(RoomType.TRADE, itemId, buyerId, sellerId);
            return chatRoomRepository.save(newRoom);
        });
    }

    // 2. 이전 대화 기록 불러오기
    @Transactional(readOnly = true)
    public List<ChatMessage> getChatHistory(String roomId) {
        return chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId);
    }

    // 3. 관리자 1:1 문의 채팅방 생성 또는 조회
    @Transactional
    public ChatRoom createOrGetAdminRoom(String userId) {
        // 이미 해당 유저와 관리자 간의 방이 있는지 확인
        return chatRoomRepository.findByRoomTypeAndItemIdAndBuyerIdAndSellerId(
                RoomType.ADMIN, null, userId, "admin"
        ).orElseGet(() -> {
            // 없으면 새로 생성해서 DB에 저장
            ChatRoom newRoom = ChatRoom.create(RoomType.ADMIN, null, userId, "admin");
            return chatRoomRepository.save(newRoom);
        });
    }

    public List<ChatRoom> findAllRooms() {
        // JPA가 제공하는 기본 메서드인 findAll()을 사용해 테이블의 모든 데이터를 가져옵니다.
        return chatRoomRepository.findAll();
    }

    /**
     * [중고거래 전용] 1:1 채팅방 생성 또는 기존 방 조회
     */
    @Transactional
    public ChatRoom createOrGetTradeRoom(String buyerId, String sellerId, Long itemId) {

        // 1. 이미 이 물건에 대해 구매자가 열어둔 방이 있는지 DB에서 찾습니다.
        return chatRoomRepository.findByItemIdAndBuyerId(itemId, buyerId)

                // 2. 만약 방이 이미 존재한다면 그 방을 그대로 반환하고 끝냅니다!
                .orElseGet(() -> {

                    // 3. 방이 없다면? 새로 만들어줍니다.
                    // (기존 관리자 방 만들 때 쓰셨던 ChatRoom.create 메서드 순서에 맞게 넣습니다.)
                    // 파라미터 순서: (방 타입, 상품 번호, 구매자 아이디, 판매자 아이디)
                    ChatRoom newRoom = ChatRoom.create(RoomType.TRADE, itemId, buyerId, sellerId);

                    // DB에 저장하고 반환합니다.
                    return chatRoomRepository.save(newRoom);
                });
    }
}