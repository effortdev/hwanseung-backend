package com.hwanseung.backend.domain.chat.controller;

import com.hwanseung.backend.domain.chat.entity.ChatMessage;
import com.hwanseung.backend.domain.chat.entity.ChatRoom;
import com.hwanseung.backend.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatRoomController {

    private final ChatService chatService;


    // [React 호출 2] 채팅방에 입장했을 때 이전 대화 기록 불러오기
    @GetMapping("/room/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable("roomId") String roomId) {
        List<ChatMessage> messages = chatService.getChatHistory(roomId);
        return ResponseEntity.ok(messages);
    }

    // [React 호출 3] 고객센터 1:1 채팅방 생성 또는 조회
    @PostMapping("/room/admin")
//    public ResponseEntity<ChatRoom> createOrGetAdminRoom(@RequestBody Map<String, Object> request) {
//        String userId = request.get("userId").toString();
    public ResponseEntity<ChatRoom> createOrGetAdminRoom(Authentication authentication) {

        // 🚨 프론트엔드에서 데이터를 받을 필요가 아예 없습니다!
        // 시큐리티(JWT)가 검증을 끝낸 현재 접속자의 아이디를 바로 꺼냅니다.
        String userId = authentication.getName();
        // 에러 해결! Repository 대신 Service를 호출합니다.
        ChatRoom room = chatService.createOrGetAdminRoom(userId);

        return ResponseEntity.ok(room);
    }

    @GetMapping("/admin/rooms")
    public ResponseEntity<List<ChatRoom>> getAllChatRooms() {
        // ChatService에 모든 방을 조회하는 메서드(예: findAllRooms)를 호출합니다.
        List<ChatRoom> rooms = chatService.findAllRooms();
        return ResponseEntity.ok(rooms);
    }

    // 🚀 중고거래 채팅방 생성 (또는 기존 방 불러오기)
    @PostMapping("/room/trade")
    public ResponseEntity<ChatRoom> createOrGetTradeRoom(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {

        // 1. JWT 토큰에서 구매자(현재 로그인한 사람) 아이디를 꺼냅니다.
        String buyerId = authentication.getName();

        // 2. 프론트에서 보낸 상품 번호와 판매자 아이디를 꺼냅니다.
        // (프론트에서 숫자로 보낼 수도, 문자로 보낼 수도 있으니 안전하게 변환)
        Long itemId = Long.valueOf(request.get("itemId").toString());
        String sellerId = request.get("sellerId").toString();

        // 3. 서비스로 넘겨서 방을 만듭니다. (또는 이미 있는 방이면 조회)
        // 🚨 ChatService에 이 메서드를 만들어주셔야 합니다!
        ChatRoom room = chatService.createOrGetTradeRoom(buyerId, sellerId, itemId);

        return ResponseEntity.ok(room);
    }
}