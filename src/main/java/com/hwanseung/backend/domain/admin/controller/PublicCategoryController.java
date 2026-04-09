package com.hwanseung.backend.domain.admin.controller;

import com.hwanseung.backend.domain.admin.dto.CategoryDTO.Response;
import com.hwanseung.backend.domain.admin.service.AdminCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class PublicCategoryController {

    private final AdminCategoryService categoryService;

    /** 일반 유저용 카테고리 전체 목록 (활성화된 것만 반환) */
    @GetMapping
    public ResponseEntity<List<Response>> getActiveCategories() {
        // 기존 서비스 메서드를 활용하되, active 상태인 것만 필터링하여 응답
        List<Response> activeCategories = categoryService.getAllCategories().stream()
                .filter(Response::getActive)
                .collect(Collectors.toList());
        return ResponseEntity.ok(activeCategories);
    }
}
