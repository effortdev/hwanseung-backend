package com.hwanseung.backend.domain.product.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ProductCreateRequestDTO {

    private String title;
    private String category;
    private Integer price;
    private String location;
    private String content;

    private List<MultipartFile> images;
}