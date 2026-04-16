package com.hwanseung.backend.domain.product.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProductUpdateRequestDTO {

    private String title;
    private String category;
    private Integer price;
    private String location;
    private String content;
    private List<MultipartFile> newImages = new ArrayList<>();
    private List<Integer> deleteImageIds = new ArrayList<>();
}