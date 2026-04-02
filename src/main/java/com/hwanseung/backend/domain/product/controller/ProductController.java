package com.hwanseung.backend.domain.product.controller;

import com.hwanseung.backend.domain.product.dto.ProductCreateRequestDTO;
import com.hwanseung.backend.domain.product.dto.ProductDetailResponseDTO;
import com.hwanseung.backend.domain.product.dto.ProductListResponseDTO;
import com.hwanseung.backend.domain.product.entity.Product;
import com.hwanseung.backend.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // 프론트 주소에 맞게 수정
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

    // 상품 목록 조회
    @GetMapping
    public ResponseEntity<List<ProductListResponseDTO>> getProductList() {
        List<ProductListResponseDTO> productList = productService.getProductList();
        return ResponseEntity.ok(productList);
    }

    // 상품 상세 조회
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponseDTO> getProductDetail(@PathVariable Integer productId) {
        System.out.println("👉 상세 조회 요청 id = " + productId);
        ProductDetailResponseDTO productDetail = productService.getProductDetail(productId);
        return ResponseEntity.ok(productDetail);
    }
}