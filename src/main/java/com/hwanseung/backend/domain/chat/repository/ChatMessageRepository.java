package com.hwanseung.backend.domain.chat.repository;

import com.hwanseung.backend.domain.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByRoomIdOrderByCreatedAtAsc(String roomId);

    int countByRoomIdAndSenderIdNotAndIsReadFalse(String roomId, String userId);

    @Modifying
    @Query("UPDATE ChatMessage c SET c.isRead = true WHERE c.roomId = :roomId AND c.senderId != :userId AND c.isRead = false")
    void markMessagesAsRead(@Param("roomId") String roomId, @Param("userId") String userId);
}