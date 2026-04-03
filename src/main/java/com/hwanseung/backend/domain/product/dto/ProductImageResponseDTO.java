package com.hwanseung.backend.domain.product.dto;

import com.hwanseung.backend.domain.product.entity.ProductImage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductImageResponseDTO {

    private Integer productImageId;
    private String imagePath;
    private String originalName;
    private String storedName;

    public static ProductImageResponseDTO from(ProductImage productImage) {
        return ProductImageResponseDTO.builder()
                .productImageId(productImage.getProductImageId())
                .imagePath(productImage.getImagePath())
                .originalName(productImage.getOriginalName())
                .storedName(productImage.getStoredName())
                .build();
    }
}