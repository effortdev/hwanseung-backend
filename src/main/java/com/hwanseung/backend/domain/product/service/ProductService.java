package com.hwanseung.backend.domain.product.service;

import com.hwanseung.backend.domain.product.dto.ProductCreateRequestDTO;
import com.hwanseung.backend.domain.product.dto.ProductCreateResponseDTO;
import com.hwanseung.backend.domain.product.entity.Product;
import com.hwanseung.backend.domain.product.repository.ProductRepository;
import com.hwanseung.backend.domain.user.entity.User;
import com.hwanseung.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductCreateResponseDTO createProduct(ProductCreateRequestDTO requestDTO, Long loginUserPk) {

        User loginUser = userRepository.findById(loginUserPk)
                .orElseThrow(() -> new RuntimeException("로그인 사용자를 찾을 수 없습니다."));

        Product product = Product.builder()
                .title(requestDTO.getTitle())
                .category(requestDTO.getCategory())
                .price(requestDTO.getPrice())
                .location(requestDTO.getLocation())
                .content(requestDTO.getContent())
                .sellerId(loginUser.getUserid())   // ✅ user 테이블의 userid 저장
                .build();

        Product savedProduct = productRepository.save(product);

        return ProductCreateResponseDTO.builder()
                .productId(savedProduct.getProductId())
                .title(savedProduct.getTitle())
                .category(savedProduct.getCategory())
                .price(savedProduct.getPrice())
                .location(savedProduct.getLocation())
                .content(savedProduct.getContent())
                .sellerId(loginUser.getUserid())         // ✅ 저장된 판매자 아이디
                .sellerNickname(loginUser.getNickname()) // ✅ 화면 표시용 닉네임
                .message("상품 등록 성공")
                .build();
    }
}