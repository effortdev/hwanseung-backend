package com.hwanseung.backend.domain.chat.repository;

import com.hwanseung.backend.domain.chat.entity.ChatRoom;
import com.hwanseung.backend.domain.chat.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    Optional<ChatRoom> findByRoomTypeAndItemIdAndBuyerIdAndSellerId(
            RoomType roomType, Long itemId, String buyerId, String sellerId
    );

    Optional<ChatRoom> findByItemIdAndBuyerId(Long itemId, String buyerId);

    List<ChatRoom> findByBuyerIdOrSellerId(String buyerId, String sellerId);

    long countByItemIdAndRoomType(Long itemId, RoomType roomType);
}