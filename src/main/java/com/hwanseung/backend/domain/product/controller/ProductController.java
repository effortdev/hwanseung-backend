package com.hwanseung.backend.domain.product.controller;

import com.hwanseung.backend.domain.product.dto.ProductCreateRequestDTO;
import com.hwanseung.backend.domain.product.dto.ProductCreateResponseDTO;
import com.hwanseung.backend.domain.product.service.ProductService;
import com.hwanseung.backend.domain.user.config.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // 프론트 주소에 맞게 수정
public class ProductController {

    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            // [수정] @RequestBody 삭제
            // [수정] @ModelAttribute로 변경
            @ModelAttribute ProductCreateRequestDTO requestDto,
            Authentication authentication
    ) throws IOException {

        Integer productId = productService.createProduct(requestDto, authentication);

        return ResponseEntity.ok(Map.of(
                "message", "상품 등록 완료",
                "productId", productId
        ));
    }
}