package com.hwanseung.backend.domain.product.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductUpdateRequestDTO {

    private String title;
    private String category;
    private Integer price;
    private String location;
    private String content;
}