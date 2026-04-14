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

    /** 상품 목록 조회 */
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

    /** 상품 상세 조회 */
    @GetMapping("/{productId}")
    public ResponseEntity<DetailResponse> getProductDetail(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductDetail(productId));
    }

    /** 상품 숨김 */
    @PatchMapping("/{productId}/hide")
    public ResponseEntity<Void> hideProduct(
            @PathVariable Long productId,
            @RequestBody ReasonRequest request) {
        productService.hideProduct(productId, request.getReason());
        return ResponseEntity.ok().build();
    }

    /** 상품 숨김 해제 */
    @PatchMapping("/{productId}/unhide")
    public ResponseEntity<Void> unhideProduct(@PathVariable Long productId) {
        productService.unhideProduct(productId);
        return ResponseEntity.ok().build();
    }

    /** 상품 삭제 */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }

    /** 일괄 삭제 */
    @DeleteMapping("/bulk")
    public ResponseEntity<Void> bulkDelete(@RequestBody BulkRequest request) {
        productService.bulkDelete(request.getProductIds());
        return ResponseEntity.ok().build();
    }
}
