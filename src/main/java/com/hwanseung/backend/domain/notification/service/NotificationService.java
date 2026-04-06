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

    // 1. 내 알림 목록 최신순으로 가져오기
    public List<Notification> getMyNotifications(String userId) {
        return notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId);
    }

    // 2. 알림 읽음 처리하기
    @Transactional
    public void markAsRead(Long notificationId, String userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다."));

        // 🚨 보안 필수: 남의 알림을 내 맘대로 읽음 처리하면 안 됨!
        if (!notification.getReceiverId().equals(userId)) {
            throw new IllegalArgumentException("이 알림에 대한 권한이 없습니다.");
        }

        // 엔티티에 만들어둔 읽음 처리 메서드 실행
        notification.markAsRead();

        // 💡 꿀팁: 클래스 위에 @Transactional이 붙어있기 때문에,
        // 값을 바꾸기만 해도 Spring Data JPA가 알아서 DB에 UPDATE 쿼리를 날려줍니다! (Dirty Checking)
    }

    // 3. 모든 알림 한 번에 읽음 처리하기
    @Transactional
    public void markAllAsRead(String userId) {
        // 내 알림 목록을 다 가져와서
        List<Notification> myNotifications = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId);
        // 안 읽은 것들을 전부 읽음(true)으로 바꿉니다! (JPA Dirty Checking으로 자동 업데이트 됨)
        for (Notification noti : myNotifications) {
            if (!noti.isRead()) {
                noti.markAsRead();
            }
        }
    }

    // 4. 특정 알림 삭제하기
    @Transactional
    public void deleteNotification(Long notificationId, String userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다."));

        // 🚨 보안 필수: 남의 알림을 지우면 안 됨!
        if (!notification.getReceiverId().equals(userId)) {
            throw new IllegalArgumentException("이 알림을 삭제할 권한이 없습니다.");
        }

        notificationRepository.delete(notification);
    }
}