package com.hwanseung.backend.domain.admin.controller;

import com.hwanseung.backend.domain.admin.dto.AdminCreateRequestDto;
import com.hwanseung.backend.domain.admin.dto.AdminRoleUpdateDto;
import com.hwanseung.backend.domain.admin.dto.AdminUserResponseDto;
import com.hwanseung.backend.domain.admin.service.AdminManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/manage")
@RequiredArgsConstructor
public class AdminManageController {

    private final AdminManageService adminManageService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('SUPER')")
    public ResponseEntity<List<AdminUserResponseDto>> getAdminList() {
        List<AdminUserResponseDto> list = adminManageService.getAdminList();
        return ResponseEntity.ok(list);
    }

    @PutMapping("/role")
    @PreAuthorize("hasRole('SUPER')")
    public ResponseEntity<Map<String, String>> updateRole(@RequestBody AdminRoleUpdateDto dto) {
        adminManageService.updateRole(dto);
        return ResponseEntity.ok(Map.of("message", "권한이 수정되었습니다."));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('SUPER')")
    public ResponseEntity<Map<String, String>> createAdmin(@RequestBody AdminCreateRequestDto dto) {
        adminManageService.createAdmin(dto);
        return ResponseEntity.ok(Map.of("message", "관리자 계정이 생성되었습니다."));
    }
}
