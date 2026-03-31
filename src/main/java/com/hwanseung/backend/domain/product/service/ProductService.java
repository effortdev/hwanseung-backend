package com.hwanseung.backend.domain.product.service;

import com.hwanseung.backend.domain.product.dto.ProductCreateRequestDTO;
import com.hwanseung.backend.domain.product.dto.ProductCreateResponseDTO;
import com.hwanseung.backend.domain.product.entity.Product;
import com.hwanseung.backend.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductCreateResponseDTO createProduct(ProductCreateRequestDTO requestDTO) {

        Product product = Product.builder()
                .title(requestDTO.getTitle())
                .category(requestDTO.getCategory())
                .price(requestDTO.getPrice())
                .location(requestDTO.getLocation())
                .content(requestDTO.getContent())
                .sellerId("testSeller")
                .build();

        Product savedProduct = productRepository.save(product);

        return ProductCreateResponseDTO.builder()
                .productId(savedProduct.getProductId())
                .message("상품 등록 성공")
                .build();
    }
}