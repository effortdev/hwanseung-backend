package com.hwanseung.backend.domain.product.controller;

import com.hwanseung.backend.domain.product.dto.ProductCreateRequestDTO;
import com.hwanseung.backend.domain.product.dto.ProductCreateResponseDTO;
import com.hwanseung.backend.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // 프론트 주소에 맞게 수정
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ProductCreateResponseDTO createProduct(@RequestBody ProductCreateRequestDTO requestDTO) {
        return productService.createProduct(requestDTO);
    }
}