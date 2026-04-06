package com.hwanseung.backend.domain.notification.repository;

import com.hwanseung.backend.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 1. 특정 유저의 알림 목록을 최신순으로 가져오기 (나중에 알림 목록 창 띄울 때 사용)
    List<Notification> findByReceiverIdOrderByCreatedAtDesc(String receiverId);

    // 2. 특정 유저의 '안 읽은' 알림 개수 가져오기 (빨간 점 뱃지 띄울 때 사용)
    long countByReceiverIdAndIsReadFalse(String receiverId);
}