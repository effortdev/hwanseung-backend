package com.hwanseung.backend.domain.chat.repository;

import com.hwanseung.backend.domain.chat.entity.ChatRoom;
import com.hwanseung.backend.domain.chat.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    // 중고거래 채팅방: 같은 상품(itemId)에 대해 같은 구매자(buyerId)와 판매자(sellerId)가 이미 만든 방이 있는지 찾습니다.
    // 방이 중복으로 여러 개 생성되는 것을 막기 위한 아주 중요한 메서드입니다!
    Optional<ChatRoom> findByRoomTypeAndItemIdAndBuyerIdAndSellerId(
            RoomType roomType, Long itemId, String buyerId, String sellerId
    );

    // [중고거래] 특정 상품에 대해 특정 구매자가 이미 만들어둔 방이 있는지 찾습니다.
    Optional<ChatRoom> findByItemIdAndBuyerId(Long itemId, String buyerId);

    // 🚀 [추가] 내가 구매자이거나 판매자인 채팅방 목록 모두 조회!
    List<ChatRoom> findByBuyerIdOrSellerId(String buyerId, String sellerId);
}