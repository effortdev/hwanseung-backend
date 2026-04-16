package com.hwanseung.backend.domain.chat.controller;

import com.hwanseung.backend.domain.chat.dto.ChatRoomListResponseDTO;
import com.hwanseung.backend.domain.chat.entity.ChatMessage;
import com.hwanseung.backend.domain.chat.entity.ChatRoom;
import com.hwanseung.backend.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${custom.upload-path}")
    private String baseUploadPath;


    @GetMapping("/room/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> getChatHistory(
            @PathVariable("roomId") String roomId,
            Authentication authentication
    ) {
        String userId = authentication.getName();

        List<ChatMessage> messages = chatService.getChatHistory(roomId, userId);

        return ResponseEntity.ok(messages);
    }

    @PostMapping("/room/admin")
    public ResponseEntity<ChatRoom> createOrGetAdminRoom(Authentication authentication) {

        String userId = authentication.getName();
        ChatRoom room = chatService.createOrGetAdminRoom(userId);

        return ResponseEntity.ok(room);
    }

    @GetMapping("/admin/rooms")
    public ResponseEntity<List<ChatRoom>> getAllChatRooms() {
        List<ChatRoom> rooms = chatService.findAllRooms();
        return ResponseEntity.ok(rooms);
    }


    @PostMapping("/room/trade")
    public ResponseEntity<ChatRoom> createOrGetTradeRoom(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {


        if (authentication == null) {
            throw new RuntimeException("로그인 정보가 없습니다.");
        }
        String buyerId = authentication.getName();

        if (!request.containsKey("itemId") || request.get("itemId") == null) {
            throw new IllegalArgumentException("itemId가 누락되었습니다.");
        }
        if (!request.containsKey("sellerId") || request.get("sellerId") == null) {
            throw new IllegalArgumentException("sellerId가 누락되었습니다.");
        }

        Long itemId = Long.valueOf(request.get("itemId").toString());
        String sellerId = request.get("sellerId").toString();


        ChatRoom room = chatService.createOrGetTradeRoom(buyerId, sellerId, itemId);


        return ResponseEntity.ok(room);
    }

    @PostMapping("/image")
    public ResponseEntity<String> uploadChatImage(@RequestParam("file") MultipartFile file) throws IOException {

        String uploadDir = baseUploadPath + "chat/";

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String originalName = file.getOriginalFilename();
        String storedName = UUID.randomUUID() + "_" + originalName;
        File dest = new File(dir, storedName);

        file.transferTo(dest);

        String imagePath = "/api/imgs/chat/" + storedName;

        return ResponseEntity.ok(imagePath);
    }

    @GetMapping("/my-rooms")
    public ResponseEntity<List<ChatRoomListResponseDTO>> getMyChatRooms(Authentication authentication) {
        String userId = authentication.getName();
        List<ChatRoomListResponseDTO> myRooms = chatService.getMyChatRooms(userId);
        return ResponseEntity.ok(myRooms);
    }
}