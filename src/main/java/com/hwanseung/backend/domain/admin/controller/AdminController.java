package com.hwanseung.backend.domain.admin.controller;


import com.hwanseung.backend.domain.admin.dto.UserResponseDto;
import com.hwanseung.backend.domain.admin.dto.UserStatusRequest;
import com.hwanseung.backend.domain.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    // --- 2번: 사용자 관리 API ---
    @GetMapping("/admin/users")
    public ResponseEntity<Page<UserResponseDto>> getUserList(
            @RequestParam(required = false) String keyword,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        // 1번만 호출되도록 변수에 할당 (이중 쿼리 실행 방지)
        Page<UserResponseDto> response = adminService.getUserList(keyword, pageable);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/admin/users/{id}/status")
    public ResponseEntity<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestBody UserStatusRequest request) {
        adminService.updateUserStatus(id, request.status());
        return ResponseEntity.noContent().build();
    }
}
