package com.hwanseung.backend.domain.chat.controller;

import com.hwanseung.backend.domain.chat.entity.ChatMessage;
import com.hwanseung.backend.domain.chat.entity.ChatRoom;
import com.hwanseung.backend.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatRoomController {

    private final ChatService chatService;


    // [React 호출 1] 상품 페이지에서 '채팅하기' 버튼 클릭 시
    // 요청 데이터 예시: { "itemId": 1, "buyerId": "user1", "sellerId": "user2" }
    @PostMapping("/room/trade")
    public ResponseEntity<ChatRoom> createOrGetTradeRoom(@RequestBody Map<String, Object> request) {
        Long itemId = Long.valueOf(request.get("itemId").toString());
        String buyerId = request.get("buyerId").toString();
        String sellerId = request.get("sellerId").toString();

        ChatRoom room = chatService.createOrGetTradeRoom(itemId, buyerId, sellerId);
        return ResponseEntity.ok(room);
    }

    // [React 호출 2] 채팅방에 입장했을 때 이전 대화 기록 불러오기
    @GetMapping("/room/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable("roomId") String roomId) {
        List<ChatMessage> messages = chatService.getChatHistory(roomId);
        return ResponseEntity.ok(messages);
    }

    // [React 호출 3] 고객센터 1:1 채팅방 생성 또는 조회
    @PostMapping("/room/admin")
    public ResponseEntity<ChatRoom> createOrGetAdminRoom(@RequestBody Map<String, Object> request) {
        String userId = request.get("userId").toString();

        // 에러 해결! Repository 대신 Service를 호출합니다.
        ChatRoom room = chatService.createOrGetAdminRoom(userId);

        return ResponseEntity.ok(room);
    }
}