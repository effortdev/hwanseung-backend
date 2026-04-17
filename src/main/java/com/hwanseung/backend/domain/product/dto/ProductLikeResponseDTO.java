package com.hwanseung.backend.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductLikeResponseDTO {

    private boolean liked;
    private long likeCount;
    private String message;
}