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

    @GetMapping
    public ResponseEntity<List<Response>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping
    public ResponseEntity<Response> createCategory(@RequestBody Request request) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<Response> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody Request request) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, request));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/order")
    public ResponseEntity<Void> updateOrder(@RequestBody OrderRequest request) {
        categoryService.updateOrder(request.getOrderedIds());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{categoryId}/toggle")
    public ResponseEntity<Void> toggleActive(@PathVariable Long categoryId) {
        categoryService.toggleActive(categoryId);
        return ResponseEntity.ok().build();
    }
}
