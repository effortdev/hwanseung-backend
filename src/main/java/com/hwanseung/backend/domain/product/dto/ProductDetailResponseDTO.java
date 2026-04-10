package com.hwanseung.backend.domain.product.dto;

import com.hwanseung.backend.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProductDetailResponseDTO {

    private Integer productId;
    private String title;
    private String category;
    private int price;
    private String location;
    private String content;
    private String sellerId;
    private String sellerNickname;
    private String saleStatus;
    private int viewCount;
    private List<ProductImageResponseDTO> productImages;

    public static ProductDetailResponseDTO from(Product product) {
        return ProductDetailResponseDTO.builder()
                .productId(product.getProductId())
                .title(product.getTitle())
                .category(product.getCategory())
                .price(product.getPrice())
                .location(product.getLocation())
                .content(product.getContent())
                .sellerId(product.getSellerId())
                .sellerNickname(product.getSellerNickname())
                .saleStatus(product.getSaleStatus())
                .viewCount(product.getViewCount())
                .productImages(
                        product.getProductImages().stream()
                                .map(ProductImageResponseDTO::from)
                                .toList()
                )
                .build();
    }
}