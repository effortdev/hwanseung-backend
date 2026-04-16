package com.hwanseung.backend.domain.admin.dto;

import lombok.*;

import java.util.List;

public class CategoryDTO {

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

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class Request {
        private String key;
        private String displayName;
        private String emoji;
        private String description;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class OrderRequest {
        private List<Long> orderedIds;
    }
}
