package com.hwanseung.backend.domain.chat.repository;

import com.hwanseung.backend.domain.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // 특정 채팅방(roomId)의 이전 대화 기록을 시간 순서대로(과거->최신) 불러옵니다.
    List<ChatMessage> findByRoomIdOrderByCreatedAtAsc(String roomId);
}