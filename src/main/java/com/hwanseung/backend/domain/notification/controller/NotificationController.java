package com.hwanseung.backend.domain.notification.controller;

import com.hwanseung.backend.domain.notification.entity.Notification;
import com.hwanseung.backend.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    // 1. 헤더가 렌더링될 때 내 과거 알림 목록 싹 다 불러오기
    // GET: http://localhost/api/notifications
    @GetMapping
    public ResponseEntity<List<Notification>> getMyNotifications(Authentication authentication) {
        // JwtTokenFilter가 검증해 준 내 아이디를 안전하게 꺼냅니다.
        String userId = authentication.getName();

        List<Notification> notifications = notificationService.getMyNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    // 2. 드롭다운에서 특정 알림을 클릭했을 때 '읽음' 처리하기
    // PUT: http://localhost/api/notifications/{알림번호}/read
    @PutMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(@PathVariable("id") Long id, Authentication authentication) {
        String userId = authentication.getName();

        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok("읽음 처리 완료");
    }

    // 3. 모든 알림 한 번에 읽음 처리하기
    @PutMapping("/read-all")
    public ResponseEntity<String> markAllAsRead(Authentication authentication) {
        String userId = authentication.getName();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok("전체 읽음 처리 완료");
    }

    // 4. 특정 알림 삭제하기
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable("id") Long id, Authentication authentication) {
        String userId = authentication.getName();
        notificationService.deleteNotification(id, userId);
        return ResponseEntity.ok("알림 삭제 완료");
    }
}