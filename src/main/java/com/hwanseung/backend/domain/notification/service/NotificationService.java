package com.hwanseung.backend.domain.notification.service;

import com.hwanseung.backend.domain.notification.entity.Notification;
import com.hwanseung.backend.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<Notification> getMyNotifications(String userId) {
        return notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId, String userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다."));

        if (!notification.getReceiverId().equals(userId)) {
            throw new IllegalArgumentException("이 알림에 대한 권한이 없습니다.");
        }

        notification.markAsRead();

    }

    @Transactional
    public void markAllAsRead(String userId) {
        List<Notification> myNotifications = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId);
        for (Notification noti : myNotifications) {
            if (!noti.isRead()) {
                noti.markAsRead();
            }
        }
    }

    @Transactional
    public void deleteNotification(Long notificationId, String userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다."));

        if (!notification.getReceiverId().equals(userId)) {
            throw new IllegalArgumentException("이 알림을 삭제할 권한이 없습니다.");
        }

        notificationRepository.delete(notification);
    }
}