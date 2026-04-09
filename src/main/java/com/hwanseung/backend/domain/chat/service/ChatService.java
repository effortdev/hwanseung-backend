package com.hwanseung.backend.domain.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwanseung.backend.domain.chat.dto.ChatRoomListResponseDTO;
import com.hwanseung.backend.domain.chat.entity.ChatMessage;
import com.hwanseung.backend.domain.chat.entity.ChatRoom;
import com.hwanseung.backend.domain.chat.entity.RoomType;
import com.hwanseung.backend.domain.chat.repository.ChatMessageRepository;
import com.hwanseung.backend.domain.chat.repository.ChatRoomRepository;
import com.hwanseung.backend.domain.product.entity.Product;
import com.hwanseung.backend.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ProductRepository productRepository;

    // 🚀 [추가] Redis에 저장된 최신 메시지를 훔쳐(?) 오기 위한 도구들
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

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

            // 🚨 [1] 관리자 1:1 채팅방(itemId가 없는 방) 예외 처리
            if (room.getItemId() == null || room.getItemId() == 0L) {
                int unreadAdmin = chatMessageRepository.countByRoomIdAndSenderIdNotAndIsReadFalse(room.getRoomId(), userId);
                return ChatRoomListResponseDTO.builder()
                        .roomId(room.getRoomId())
                        .buyerId(room.getBuyerId())
                        .sellerId("admin")
                        .itemName("1:1 문의")
                        .lastMessage("문의 채팅방입니다.")
                        .unreadCount(unreadAdmin)
                        .build();
            }

            // 🚨 [2] 일반 중고거래 방: 여기가 아까 누락되었던 'realItemName'을 만드는 곳입니다!
            Product product = productRepository.findById(room.getItemId().intValue()).orElse(null);
            String realItemName = (product != null) ? product.getTitle() : "삭제된 상품입니다.";

            // 🚨 [3] 안 읽은 메시지 개수 세기!
            int unreadMessages = chatMessageRepository.countByRoomIdAndSenderIdNotAndIsReadFalse(room.getRoomId(), userId);

            // 🚨 [4] DTO 조립
            return ChatRoomListResponseDTO.builder()
                    .roomId(room.getRoomId())
                    .buyerId(room.getBuyerId())
                    .sellerId(room.getSellerId())
                    .itemName(realItemName) // 💡 이제 위에서 찾은 이름을 쏙 넣습니다! 에러 해결!
                    .lastMessage("최신 대화 내역이 없습니다.")
                    .unreadCount(unreadMessages) // 💡 진짜 안 읽은 숫자 세팅!
                    .build();

        }).toList(); // Java 16 이상이면 .toList() 사용 (11 이하면 .collect(Collectors.toList()) 사용)
    }

    @Transactional // 🚨 업데이트 쿼리가 있으므로 트랜잭션 필수!
    public List<ChatMessage> getChatHistory(String roomId, String userId) {

        // 1. 방에 들어왔으니, 이 방에서 상대방이 보낸 메시지를 전부 읽음(true) 처리합니다!
        chatMessageRepository.markMessagesAsRead(roomId, userId);

        // 2. 일단 진짜 DB(MariaDB)에서 안전하게 보관된 과거 기록을 싹 가져옵니다.
        // 💡 주의: List가 변경 가능해야 하므로 new ArrayList<> 로 감싸줍니다!
        List<ChatMessage> history = new ArrayList<>(chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId));


        // ==========================================================
        // 🚀 3. "Redis 임시 보관함(chat_messages_buffer)"을 뒤집니다!
        // ==========================================================
        try {
            // 💡 회원님의 RedisSubscriber에 적혀있던 바로 그 키 이름!
            String redisKey = "chat_messages_buffer";

            // Redis에서 해당 키에 쌓인 리스트를 전부 가져옵니다.
            List<Object> redisMessages = redisTemplate.opsForList().range(redisKey, 0, -1);

            if (redisMessages != null) {
                for (Object obj : redisMessages) {
                    // RedisSubscriber 코드를 보니 JSON 문자열로 저장하셨네요! String으로 꺼냅니다.
                    String jsonStr = (String) obj;

                    // JSON 문자열을 파싱해서 데이터를 안전하게 꺼냅니다.
                    com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(jsonStr);

                    String cachedRoomId = jsonNode.has("roomId") ? jsonNode.get("roomId").asText() : "";

                    // 🚨 가져온 메시지가 "지금 내가 들어온 이 방(roomId)"의 메시지일 때만 처리!
                    if (roomId.equals(cachedRoomId)) {

                        // DB에 넣을 때처럼 ChatMessage 엔티티를 임시로 만듭니다.
                        ChatMessage cachedMsg = new ChatMessage();
                        cachedMsg.setRoomId(cachedRoomId);

                        // 프론트에서 sender로 보냈을 수도 있고 senderId로 보냈을 수도 있으니 둘 다 체크!
                        String sender = jsonNode.has("sender") ? jsonNode.get("sender").asText() :
                                (jsonNode.has("senderId") ? jsonNode.get("senderId").asText() : "");
                        cachedMsg.setSenderId(sender);

                        cachedMsg.setContent(jsonNode.has("content") ? jsonNode.get("content").asText() : "");

                        // 방금 가져온 거니까 내가 보낸 게 아니면 무조건 읽은 걸로 강제 취급!
                        if (!userId.equals(sender)) {
                            cachedMsg.setRead(true);
                        }

                        // DB 내역 뒤에 방금 친 Redis 내역을 이어 붙입니다!
                        history.add(cachedMsg);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Redis에서 최신 채팅 불러오기 실패 (DB 내역만 반환): " + e.getMessage());
        }
        // ==========================================================

        // 4. 과거 기록(DB)과 방금 친 기록(Redis)이 완벽하게 시간순으로 합쳐진 리스트를 프론트엔드에 던져줍니다!
        return history;
        // 2. 그리고 대화 기록을 싹 불러와서 프론트로 던져줍니다.
//        return chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId);
    }
}