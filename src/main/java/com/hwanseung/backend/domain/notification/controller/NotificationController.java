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

    @GetMapping
    public ResponseEntity<List<Notification>> getMyNotifications(Authentication authentication) {
        String userId = authentication.getName();

        List<Notification> notifications = notificationService.getMyNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(@PathVariable("id") Long id, Authentication authentication) {
        String userId = authentication.getName();

        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok("읽음 처리 완료");
    }

    @PutMapping("/read-all")
    public ResponseEntity<String> markAllAsRead(Authentication authentication) {
        String userId = authentication.getName();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok("전체 읽음 처리 완료");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable("id") Long id, Authentication authentication) {
        String userId = authentication.getName();
        notificationService.deleteNotification(id, userId);
        return ResponseEntity.ok("알림 삭제 완료");
    }
}