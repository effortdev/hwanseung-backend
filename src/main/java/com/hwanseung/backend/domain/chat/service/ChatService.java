package com.hwanseung.backend.domain.chat.service;

import com.hwanseung.backend.domain.chat.dto.ChatRoomListResponseDTO;
import com.hwanseung.backend.domain.chat.entity.ChatMessage;
import com.hwanseung.backend.domain.chat.entity.ChatRoom;
import com.hwanseung.backend.domain.chat.entity.RoomType;
import com.hwanseung.backend.domain.chat.repository.ChatMessageRepository;
import com.hwanseung.backend.domain.chat.repository.ChatRoomRepository;
import com.hwanseung.backend.domain.product.entity.Product;
import com.hwanseung.backend.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ProductRepository productRepository;

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

    @Transactional(readOnly = true)
    public List<ChatRoom> findMyRooms(String userId) {
        // 구매자로 참여한 방이거나, 판매자로 참여한 방을 모두 찾아서 반환합니다.
        return chatRoomRepository.findByBuyerIdOrSellerId(userId, userId);
    }

    @Transactional(readOnly = true)
    public List<ChatRoomListResponseDTO> getMyChatRooms(String userId) {
        // 1. 내가 속한 방 원본 목록 가져오기
        List<ChatRoom> rooms = chatRoomRepository.findByBuyerIdOrSellerId(userId, userId);

        // 2. DTO로 예쁘게 변환하기
        return rooms.stream().map(room -> {

            // 🚨 관리자 1:1 채팅방(itemId가 없는 방) 예외 처리
            if (room.getItemId() == null || room.getItemId() == 0L) {
                return ChatRoomListResponseDTO.builder()
                        .roomId(room.getRoomId())
                        .buyerId(room.getBuyerId())
                        .sellerId("admin")
                        .itemName("1:1 문의")
                        .lastMessage("문의 채팅방입니다.")
                        .unreadCount(0)
                        .build();
            }

            // 🚨 일반 중고거래 방: itemId로 상품 테이블을 조회해서 이름을 가져옵니다!
            Product product = productRepository.findById(room.getItemId().intValue()).orElse(null);
            String realItemName = (product != null) ? product.getTitle() : "삭제된 상품입니다.";

            return ChatRoomListResponseDTO.builder()
                    .roomId(room.getRoomId())
                    .buyerId(room.getBuyerId())
                    .sellerId(room.getSellerId())
                    .itemName(realItemName) // 드디어 상품 이름 세팅!
                    .lastMessage("최신 대화 내역이 없습니다.") // (나중에 메시지 테이블 조회로 고도화 가능)
                    .unreadCount(0)
                    .build();
        }).toList();
    }
}