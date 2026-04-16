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

    @GetMapping
    public ResponseEntity<List<Response>> getActiveCategories() {
        List<Response> activeCategories = categoryService.getAllCategories().stream()
                .filter(Response::getActive)
                .collect(Collectors.toList());
        return ResponseEntity.ok(activeCategories);
    }
}
