package com.hwanseung.backend.domain.chat.controller;

import com.hwanseung.backend.domain.chat.dto.ChatRoomListResponseDTO;
import com.hwanseung.backend.domain.chat.entity.ChatMessage;
import com.hwanseung.backend.domain.chat.entity.ChatRoom;
import com.hwanseung.backend.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatRoomController {

    private final ChatService chatService;


    // [React 호출 2] 채팅방에 입장했을 때 이전 대화 기록 불러오기
    @GetMapping("/room/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> getChatHistory(
            @PathVariable("roomId") String roomId,
            Authentication authentication // 💡 [1] 시큐리티에서 현재 로그인한 유저 정보를 받아옵니다.
    ) {
        // 💡 [2] 토큰에서 내 아이디를 꺼냅니다.
        String userId = authentication.getName();

        // 💡 [3] 방 번호와 내 아이디를 같이 서비스로 넘겨줍니다! (읽음 처리를 위해)
        List<ChatMessage> messages = chatService.getChatHistory(roomId, userId);

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
//    @PostMapping("/room/trade")
//    public ResponseEntity<ChatRoom> createOrGetTradeRoom(
//            @RequestBody Map<String, Object> request,
//            Authentication authentication) {
//
//        // 1. JWT 토큰에서 구매자(현재 로그인한 사람) 아이디를 꺼냅니다.
//        String buyerId = authentication.getName();
//
//        // 2. 프론트에서 보낸 상품 번호와 판매자 아이디를 꺼냅니다.
//        // (프론트에서 숫자로 보낼 수도, 문자로 보낼 수도 있으니 안전하게 변환)
//        Long itemId = Long.valueOf(request.get("itemId").toString());
//        String sellerId = request.get("sellerId").toString();
//
//        // 3. 서비스로 넘겨서 방을 만듭니다. (또는 이미 있는 방이면 조회)
//        // 🚨 ChatService에 이 메서드를 만들어주셔야 합니다!
//        ChatRoom room = chatService.createOrGetTradeRoom(buyerId, sellerId, itemId);
//
//        return ResponseEntity.ok(room);
//    }

    // 🚀 중고거래 채팅방 생성 (또는 기존 방 불러오기)
    @PostMapping("/room/trade")
    public ResponseEntity<ChatRoom> createOrGetTradeRoom(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {

        // 🚨 1. 프론트에서 데이터가 제대로 넘어왔는지 확인하는 콘솔 출력!
        System.out.println("👉 프론트에서 도착한 원본 데이터: " + request);

        // 🚨 2. 로그인 안 된 유저가 찌르는 것 방지
        if (authentication == null) {
            System.out.println("❌ 에러: 로그인 정보(Authentication)가 없습니다!");
            throw new RuntimeException("로그인 정보가 없습니다.");
        }
        String buyerId = authentication.getName();

        // 🚨 3. null 방어 로직 (이게 없으면 .toString() 에서 터집니다)
        if (!request.containsKey("itemId") || request.get("itemId") == null) {
            System.out.println("❌ 에러: 프론트에서 itemId를 안 보냈습니다!");
            throw new IllegalArgumentException("itemId가 누락되었습니다.");
        }
        if (!request.containsKey("sellerId") || request.get("sellerId") == null) {
            System.out.println("❌ 에러: 프론트에서 sellerId를 안 보냈습니다!");
            throw new IllegalArgumentException("sellerId가 누락되었습니다.");
        }

        // 안전하게 변환
        Long itemId = Long.valueOf(request.get("itemId").toString());
        String sellerId = request.get("sellerId").toString();

        System.out.println("✅ 데이터 파싱 성공! - 구매자: " + buyerId + ", 판매자: " + sellerId + ", 상품번호: " + itemId);

        // 4. 서비스 로직 호출
        ChatRoom room = chatService.createOrGetTradeRoom(buyerId, sellerId, itemId);

        System.out.println("✅ 방 생성 완료! 방 번호: " + room.getRoomId());

        return ResponseEntity.ok(room);
    }

    // 🚀 [채팅 전용] 이미지 업로드 API
    // 프론트 요청 경로: POST /api/chat/image
    @PostMapping("/image")
    public ResponseEntity<String> uploadChatImage(@RequestParam("file") MultipartFile file) throws IOException {

        // 1. 채팅 이미지를 모아둘 별도 폴더 지정 (상품 이미지와 섞이지 않도록 chat 폴더 사용)
//        String uploadDir = "C:/bImg/chat/";
        // 🛠️ 수정: 윈도우 경로를 리눅스 경로로 변경
        String uploadDir = "/app/uploads/chat/";

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 2. 파일 이름 중복 방지를 위해 UUID 붙이기
        String originalName = file.getOriginalFilename();
        String storedName = UUID.randomUUID() + "_" + originalName;
        File dest = new File(dir, storedName);

        // 3. C드라이브에 실제 파일 저장
        file.transferTo(dest);

        // 4. 프론트엔드가 접근할 수 있는 웹 주소 리턴 (WebConfig에 설정된 경로 활용)
        String imagePath = "/api/imgs/chat/" + storedName;

        return ResponseEntity.ok(imagePath); // 예: "/api/imgs/chat/어쩌구저쩌구.jpg" 문자열만 딱 리턴!
    }

    // 🚀 [추가] 내 채팅방 목록 불러오기 API
    // 프론트엔드 요청 경로: GET /api/chat/my-rooms
    @GetMapping("/my-rooms")
    public ResponseEntity<List<ChatRoomListResponseDTO>> getMyChatRooms(Authentication authentication) {
        String userId = authentication.getName();
        List<ChatRoomListResponseDTO> myRooms = chatService.getMyChatRooms(userId);
        return ResponseEntity.ok(myRooms);
    }
}