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

    /**
     * 관리자 목록 조회 (ROLE_SUPER, ROLE_ADMIN, ROLE_SUB)
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('SUPER')")
    public ResponseEntity<List<AdminUserResponseDto>> getAdminList() {
        List<AdminUserResponseDto> list = adminManageService.getAdminList();
        return ResponseEntity.ok(list);
    }

    /**
     * 관리자 권한 수정
     */
    @PutMapping("/role")
    @PreAuthorize("hasRole('SUPER')")
    public ResponseEntity<Map<String, String>> updateRole(@RequestBody AdminRoleUpdateDto dto) {
        adminManageService.updateRole(dto);
        return ResponseEntity.ok(Map.of("message", "권한이 수정되었습니다."));
    }

    /**
     * 관리자 계정 생성
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('SUPER')")
    public ResponseEntity<Map<String, String>> createAdmin(@RequestBody AdminCreateRequestDto dto) {
        adminManageService.createAdmin(dto);
        return ResponseEntity.ok(Map.of("message", "관리자 계정이 생성되었습니다."));
    }
}
