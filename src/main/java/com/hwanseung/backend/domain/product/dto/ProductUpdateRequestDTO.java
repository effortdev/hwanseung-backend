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
    // 새로 업로드할 이미지들
    private List<MultipartFile> newImages = new ArrayList<>();
    // 삭제할 기존 이미지 id 목록
    private List<Integer> deleteImageIds = new ArrayList<>();
}