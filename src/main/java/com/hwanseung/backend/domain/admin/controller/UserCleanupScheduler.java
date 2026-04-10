package com.hwanseung.backend.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCleanupScheduler {

    private final LoginManager loginManager;

    // 10분마다 만료된 유저 데이터를 메모리에서 삭제
    @Scheduled(fixedDelay = 600000)
    public void cleanup() {
        loginManager.clearExpiredUsers();
    }
}
