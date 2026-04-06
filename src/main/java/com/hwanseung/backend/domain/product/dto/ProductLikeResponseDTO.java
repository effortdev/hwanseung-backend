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

    private boolean liked;     // 현재 로그인 사용자의 찜 여부
    private long likeCount;    // 상품의 총 찜 개수
    private String message;    // 처리 메시지
}