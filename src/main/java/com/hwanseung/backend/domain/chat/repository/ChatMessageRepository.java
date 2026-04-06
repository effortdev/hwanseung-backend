package com.hwanseung.backend.domain.chat.repository;

import com.hwanseung.backend.domain.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // 특정 채팅방(roomId)의 이전 대화 기록을 시간 순서대로(과거->최신) 불러옵니다.
    List<ChatMessage> findByRoomIdOrderByCreatedAtAsc(String roomId);

    // 🚀 [추가 1] 특정 방에서 '내가 아닌 다른 사람'이 보낸 메시지 중, '안 읽은(false)' 메시지 개수 세기!
    int countByRoomIdAndSenderIdNotAndIsReadFalse(String roomId, String userId);

    // 🚀 [추가 2] 방에 입장하면 '내가 아닌 다른 사람'이 보낸 안 읽은 메시지를 모두 '읽음(true)'으로 강제 업데이트!
    @Modifying
    @Query("UPDATE ChatMessage c SET c.isRead = true WHERE c.roomId = :roomId AND c.senderId != :userId AND c.isRead = false")
    void markMessagesAsRead(@Param("roomId") String roomId, @Param("userId") String userId);
}