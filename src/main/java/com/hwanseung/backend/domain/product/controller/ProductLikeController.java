package com.hwanseung.backend.domain.product.controller;

import com.hwanseung.backend.domain.product.dto.ProductLikeResponseDTO;
import com.hwanseung.backend.domain.product.service.ProductLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductLikeController {

    private final ProductLikeService productLikeService;

    // 찜 추가
    @PostMapping("/{productId}/like")
    public ResponseEntity<ProductLikeResponseDTO> likeProduct(
            @PathVariable Integer productId,
            Authentication authentication
    ) {
        String loginUserId = authentication.getName(); // 로그인 사용자

        ProductLikeResponseDTO response =
                productLikeService.likeProduct(productId, loginUserId);

        return ResponseEntity.ok(response);
    }

    // 찜 취소
    @DeleteMapping("/{productId}/like")
    public ResponseEntity<ProductLikeResponseDTO> unlikeProduct(
            @PathVariable Integer productId,
            Authentication authentication
    ) {
        String loginUserId = authentication.getName();

        ProductLikeResponseDTO response =
                productLikeService.unlikeProduct(productId, loginUserId);

        return ResponseEntity.ok(response);
    }

    // 찜 상태 조회
    @GetMapping("/{productId}/like")
    public ResponseEntity<ProductLikeResponseDTO> getLikeStatus(
            @PathVariable Integer productId,
            Authentication authentication
    ) {
        // 🔥 로그인 안한 경우 null 처리 (중요)
        String loginUserId = (authentication != null) ? authentication.getName() : null;

        ProductLikeResponseDTO response =
                productLikeService.getLikeStatus(productId, loginUserId);

        return ResponseEntity.ok(response);
    }
}