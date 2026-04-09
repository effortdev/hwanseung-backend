package com.hwanseung.backend.domain.admin.controller;

import com.hwanseung.backend.domain.admin.dto.CategoryDTO.*;
import com.hwanseung.backend.domain.admin.service.AdminCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final AdminCategoryService categoryService;

    /** 카테고리 전체 목록 */
    @GetMapping
    public ResponseEntity<List<Response>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    /** 카테고리 등록 */
    @PostMapping
    public ResponseEntity<Response> createCategory(@RequestBody Request request) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    /** 카테고리 수정 */
    @PutMapping("/{categoryId}")
    public ResponseEntity<Response> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody Request request) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, request));
    }

    /** 카테고리 삭제 */
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok().build();
    }

    /** 순서 변경 */
    @PatchMapping("/order")
    public ResponseEntity<Void> updateOrder(@RequestBody OrderRequest request) {
        categoryService.updateOrder(request.getOrderedIds());
        return ResponseEntity.ok().build();
    }

    /** 활성/비활성 토글 */
    @PatchMapping("/{categoryId}/toggle")
    public ResponseEntity<Void> toggleActive(@PathVariable Long categoryId) {
        categoryService.toggleActive(categoryId);
        return ResponseEntity.ok().build();
    }
}
