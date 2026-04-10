package com.hwanseung.backend.domain.admin.controller;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginManager {
    // Key: userId, Value: Expiration Timestamp
    private final ConcurrentHashMap<String, Long> activeUsers = new ConcurrentHashMap<>();

    // 유저 활동 갱신
    public void updateActivity(String userId, long expirationMillis) {
        activeUsers.put(userId, expirationMillis);
    }

    // 현재 유효한 접속자 수 계산
    public int getActiveUserCount() {
        long now = System.currentTimeMillis();
        // 만료 시간이 지나지 않은 유저만 카운트
        return (int) activeUsers.values().stream()
                .filter(exp -> exp > now)
                .count();
    }

    // 만료된 유저 제거 (UserCleanupScheduler가 10분마다 이 메서드를 호출함)
    public void clearExpiredUsers() {
        long now = System.currentTimeMillis();
        activeUsers.entrySet().removeIf(entry -> entry.getValue() <= now);
    }
}
