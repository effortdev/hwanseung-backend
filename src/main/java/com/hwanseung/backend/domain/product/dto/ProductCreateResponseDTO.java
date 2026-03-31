package com.hwanseung.backend.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProductCreateResponseDTO {

    private Integer productId;
    private String title;
    private String category;
    private Integer price;
    private String location;
    private String content;

    private String sellerId;         // ✅ userid
    private String sellerNickname;   // ✅ nickname

    private String message;
}