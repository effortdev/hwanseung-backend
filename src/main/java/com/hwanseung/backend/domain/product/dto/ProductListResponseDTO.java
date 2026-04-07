package com.hwanseung.backend.domain.product.dto;

import com.hwanseung.backend.domain.product.entity.Product;
import com.hwanseung.backend.domain.product.entity.ProductImage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductListResponseDTO {

    private Integer productId;
    private String title;
    private String category;
    private int price;
    private String location;
    private String sellerId;
    private String sellerNickname;
    private String saleStatus;
    private String thumbnailUrl;
    private long likeCount;
    private long chatCount;
    private boolean liked;

    public static ProductListResponseDTO from(Product product, long likeCount, long chatCount, boolean liked) {
        String thumbnailUrl = null;

        if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
            ProductImage firstImage = product.getProductImages().get(0);
            thumbnailUrl = firstImage.getImagePath();
        }

        return ProductListResponseDTO.builder()
                .productId(product.getProductId())
                .title(product.getTitle())
                .category(product.getCategory())
                .price(product.getPrice())
                .location(product.getLocation())
                .sellerId(product.getSellerId())
                .sellerNickname(product.getSellerNickname())
                .saleStatus(product.getSaleStatus())
                .thumbnailUrl(thumbnailUrl)
                .likeCount(likeCount)
                .chatCount(chatCount)
                .liked(liked)
                .build();
    }
}