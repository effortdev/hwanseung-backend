package com.hwanseung.backend.domain.product.controller;

import com.hwanseung.backend.domain.product.dto.ProductCreateRequestDTO;
import com.hwanseung.backend.domain.product.dto.ProductDetailResponseDTO;
import com.hwanseung.backend.domain.product.dto.ProductListResponseDTO;
import com.hwanseung.backend.domain.product.dto.ProductUpdateRequestDTO;
import com.hwanseung.backend.domain.product.service.ProductService;
import com.hwanseung.backend.domain.user.config.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @ModelAttribute ProductCreateRequestDTO requestDto,
            Authentication authentication
    ) throws IOException {

        Integer productId = productService.createProduct(requestDto, authentication);

        return ResponseEntity.ok(Map.of(
                "message", "상품 등록 완료",
                "productId", productId
        ));
    }

    @GetMapping("/wishlist")
    public ResponseEntity<List<ProductListResponseDTO>> getWishlist(Authentication authentication) {
        CustomUserDetails loginUser = (CustomUserDetails) authentication.getPrincipal();
        List<ProductListResponseDTO> wishlist = productService.getWishlist(loginUser.getUsername());
        return ResponseEntity.ok(wishlist);
    }

    @GetMapping
    public ResponseEntity<List<ProductListResponseDTO>> getProductList(Authentication authentication) {
        String loginUserId = authentication != null ? authentication.getName() : null;

        List<ProductListResponseDTO> productList = productService.getProductList(loginUserId);
        return ResponseEntity.ok(productList);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponseDTO> getProductDetail(@PathVariable Integer productId) {
        ProductDetailResponseDTO productDetail = productService.getProductDetail(productId);
        return ResponseEntity.ok(productDetail);
    }

    // 메인페이지 인기 매물 조회
    @GetMapping("/popular")
    public ResponseEntity<List<ProductListResponseDTO>> getPopularProducts(Authentication authentication) {
        String loginUserId = authentication != null ? authentication.getName() : null;
        List<ProductListResponseDTO> popularProducts = productService.getPopularProducts(loginUserId);
        return ResponseEntity.ok(popularProducts);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getProductCount() {
        long count = productService.getTotalProductCount();
        Map<String, Long> response = new HashMap<>();
        response.put("totalCount", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sellcount")
    public ResponseEntity<Map<String, Long>> getSellProductCount() {
        long count = productService.getActiveProductCount();
        Map<String, Long> response = new HashMap<>();
        response.put("sellCount", count);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable Integer productId,
            @ModelAttribute ProductUpdateRequestDTO requestDTO,
            Authentication authentication
    ) throws IOException {
        productService.updateProduct(productId, requestDTO, authentication);

        return ResponseEntity.ok(Map.of(
                "message", "상품 수정 완료",
                "productId", productId
        ));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(
            @PathVariable Integer productId,
            Authentication authentication
    ) {
        productService.deleteProduct(productId, authentication);

        return ResponseEntity.ok(Map.of(
                "message", "상품 삭제 완료",
                "productId", productId
        ));
    }

    @PatchMapping("/{productId}/sold-out")
    public ResponseEntity<?> markProductAsSoldOut(
            @PathVariable Integer productId,
            Authentication authentication
    ) {
        productService.markProductAsSoldOut(productId, authentication);

        return ResponseEntity.ok(Map.of(
                "message", "판매완료 처리되었습니다.",
                "productId", productId
        ));
    }

    @PatchMapping("/{productId}/reserved")
    public ResponseEntity<?> markProductAsReserved(
            @PathVariable Integer productId,
            Authentication authentication
    ) {
        productService.markProductAsReserved(productId, authentication);

        return ResponseEntity.ok(Map.of(
                "message", "예약중 처리되었습니다.",
                "productId", productId
        ));
    }

    @PatchMapping("/{productId}/sale")
    public ResponseEntity<?> markProductAsSale(
            @PathVariable Integer productId,
            Authentication authentication
    ) {
        productService.markProductAsSale(productId, authentication);

        return ResponseEntity.ok(Map.of(
                "message", "판매중으로 변경되었습니다.",
                "productId", productId
        ));
    }

    @GetMapping("/my-sales")
    public ResponseEntity<List<ProductListResponseDTO>> getMySalesList(Authentication authentication) {

        String loginUserId = authentication.getName();

        List<ProductListResponseDTO> mySalesList = productService.getMySalesList(loginUserId);

        return ResponseEntity.ok(mySalesList);
    }



}