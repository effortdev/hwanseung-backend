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

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public ChatRoom createOrGetTradeRoom(Long itemId, String buyerId, String sellerId) {
        return chatRoomRepository.findByRoomTypeAndItemIdAndBuyerIdAndSellerId(
                RoomType.TRADE, itemId, buyerId, sellerId
        ).orElseGet(() -> {
            ChatRoom newRoom = ChatRoom.create(RoomType.TRADE, itemId, buyerId, sellerId);
            return chatRoomRepository.save(newRoom);
        });
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> getChatHistory(String roomId) {
        return chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId);
    }

    @Transactional
    public ChatRoom createOrGetAdminRoom(String userId) {
        return chatRoomRepository.findByRoomTypeAndItemIdAndBuyerIdAndSellerId(
                RoomType.ADMIN, null, userId, "admin"
        ).orElseGet(() -> {
            ChatRoom newRoom = ChatRoom.create(RoomType.ADMIN, null, userId, "admin");
            return chatRoomRepository.save(newRoom);
        });
    }

    public List<ChatRoom> findAllRooms() {
        return chatRoomRepository.findAll();
    }

    @Transactional
    public ChatRoom createOrGetTradeRoom(String buyerId, String sellerId, Long itemId) {

        return chatRoomRepository.findByItemIdAndBuyerId(itemId, buyerId)

                .orElseGet(() -> {

                    ChatRoom newRoom = ChatRoom.create(RoomType.TRADE, itemId, buyerId, sellerId);

                    return chatRoomRepository.save(newRoom);
                });
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> findMyRooms(String userId) {
        return chatRoomRepository.findByBuyerIdOrSellerId(userId, userId);
    }

    @Transactional(readOnly = true)
    public List<ChatRoomListResponseDTO> getMyChatRooms(String userId) {
        List<ChatRoom> rooms = chatRoomRepository.findByBuyerIdOrSellerId(userId, userId);

        return rooms.stream().map(room -> {

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

            Product product = productRepository.findById(room.getItemId().intValue()).orElse(null);
            String realItemName = (product != null) ? product.getTitle() : "삭제된 상품입니다.";

            int unreadMessages = chatMessageRepository.countByRoomIdAndSenderIdNotAndIsReadFalse(room.getRoomId(), userId);

            return ChatRoomListResponseDTO.builder()
                    .roomId(room.getRoomId())
                    .buyerId(room.getBuyerId())
                    .sellerId(room.getSellerId())
                    .itemName(realItemName)
                    .lastMessage("최신 대화 내역이 없습니다.")
                    .unreadCount(unreadMessages)
                    .build();

        }).toList();
    }

    @Transactional
    public List<ChatMessage> getChatHistory(String roomId, String userId) {

        chatMessageRepository.markMessagesAsRead(roomId, userId);

        List<ChatMessage> history = new ArrayList<>(chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId));


        try {
            String redisKey = "chat_messages_buffer";

            List<Object> redisMessages = redisTemplate.opsForList().range(redisKey, 0, -1);

            if (redisMessages != null) {
                for (Object obj : redisMessages) {
                    String jsonStr = (String) obj;

                    com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(jsonStr);

                    String cachedRoomId = jsonNode.has("roomId") ? jsonNode.get("roomId").asText() : "";

                    if (roomId.equals(cachedRoomId)) {

                        ChatMessage cachedMsg = new ChatMessage();
                        cachedMsg.setRoomId(cachedRoomId);

                        String sender = jsonNode.has("sender") ? jsonNode.get("sender").asText() :
                                (jsonNode.has("senderId") ? jsonNode.get("senderId").asText() : "");
                        cachedMsg.setSenderId(sender);

                        cachedMsg.setContent(jsonNode.has("content") ? jsonNode.get("content").asText() : "");

                        if (!userId.equals(sender)) {
                            cachedMsg.setRead(true);
                        }

                        history.add(cachedMsg);
                    }
                }
            }
        } catch (Exception e) {
        }

        return history;
    }
}