package com.hwanseung.backend.domain.admin.controller;

import com.hwanseung.backend.domain.admin.dto.AdminProductDTO.*;
import com.hwanseung.backend.domain.admin.service.AdminProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminProductService productService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "latest") String sort) {
        return ResponseEntity.ok(productService.getProducts(page, size, keyword, status, category, sort));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<DetailResponse> getProductDetail(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductDetail(productId));
    }

    @PatchMapping("/{productId}/hide")
    public ResponseEntity<Void> hideProduct(
            @PathVariable Long productId,
            @RequestBody ReasonRequest request) {
        productService.hideProduct(productId, request.getReason());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{productId}/unhide")
    public ResponseEntity<Void> unhideProduct(@PathVariable Long productId) {
        productService.unhideProduct(productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<Void> bulkDelete(@RequestBody BulkRequest request) {
        productService.bulkDelete(request.getProductIds());
        return ResponseEntity.ok().build();
    }
}
