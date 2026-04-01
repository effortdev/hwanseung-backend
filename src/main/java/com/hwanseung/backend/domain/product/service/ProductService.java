package com.hwanseung.backend.domain.product.service;

import com.hwanseung.backend.domain.product.dto.ProductCreateRequestDTO;
import com.hwanseung.backend.domain.product.dto.ProductCreateResponseDTO;
import com.hwanseung.backend.domain.product.entity.Product;
import com.hwanseung.backend.domain.product.entity.ProductImage;
import com.hwanseung.backend.domain.product.repository.ProductRepository;
import com.hwanseung.backend.domain.user.entity.User;
import com.hwanseung.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    // 실제 파일 저장 폴더
    private static final String UPLOAD_DIR = "C:/bImg/product/";

    public Integer createProduct(ProductCreateRequestDTO requestDTO, Authentication authentication) throws IOException {

        // 판매자 아이디는 로그인 사용자 기준으로 처리
        String sellerId = authentication.getName();

        Product product = Product.builder()
                .title(requestDTO.getTitle())
                .category(requestDTO.getCategory())
                .price(requestDTO.getPrice())
                .location(requestDTO.getLocation())
                .content(requestDTO.getContent())
                .sellerId(sellerId)
                .build();

        // 이미지 1장 꺼내기
        MultipartFile image = requestDTO.getImage();

        // 이미지가 있으면 파일 저장 후 Product에 연결
        if (image != null && !image.isEmpty()) {
            ProductImage productImage = saveProductImage(image);
            product.addProductImage(productImage);
        }

        Product savedProduct = productRepository.save(product);

        return savedProduct.getProductId();

    }

    // 파일 저장 + ProductImage 엔티티 생성
    private ProductImage saveProductImage(MultipartFile image) throws IOException {

        File dir = new File(UPLOAD_DIR);

        // 폴더 없으면 생성
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String originalName = image.getOriginalFilename();
        String storedName = UUID.randomUUID() + "_" + originalName;

        File dest = new File(dir, storedName);
        image.transferTo(dest);

        // 브라우저 접근용 경로
        String imagePath = "/api/imgs/product/" + storedName;

        return ProductImage.builder()
                .originalName(originalName)
                .storedName(storedName)
                .imagePath(imagePath)
                .build();
    }
}