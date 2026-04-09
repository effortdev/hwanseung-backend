package com.hwanseung.backend.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {
    private final LoginManager loginManager;

    @GetMapping("/active-users")
    public ResponseEntity<Map<String, Integer>> getActiveUsers() {
        System.out.println("ss777777777::: " + loginManager.getActiveUserCount());
        return ResponseEntity.ok(Map.of("count", loginManager.getActiveUserCount()));
    }
}
