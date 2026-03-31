package com.hwanseung.backend.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProductCreateResponseDTO {

    private Integer productId;
    private String message;
}