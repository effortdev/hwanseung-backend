package com.hwanseung.backend.domain.admin.dto;

import lombok.*;

import java.util.List;

public class CategoryDTO {

    /** 카테고리 목록 응답 */
    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class Response {
        private Long id;
        private String key;
        private String displayName;
        private String emoji;
        private String description;
        private Boolean active;
        private Integer sortOrder;
        private long productCount;
    }

    /** 카테고리 등록/수정 요청 */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class Request {
        private String key;
        private String displayName;
        private String emoji;
        private String description;
    }

    /** 순서 변경 요청 */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class OrderRequest {
        private List<Long> orderedIds;
    }
}
