package com.hwanseung.backend.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_image_id")
    private Integer productImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "original_name", nullable = false, length = 255)
    private String originalName; // 원본 파일명

    @Column(name = "stored_name", nullable = false, length = 255)
    private String storedName; // 저장 파일명

    @Column(name = "image_path", nullable = false, length = 255)
    private String imagePath; // 예: /api/imgs/product/uuid_xxx.jpg

    // Product.addProductImage()에서 사용
    public void setProduct(Product product) {
        this.product = product;
    }
}