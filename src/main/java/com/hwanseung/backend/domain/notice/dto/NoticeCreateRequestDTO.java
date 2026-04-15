package com.hwanseung.backend.domain.notice.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NoticeCreateRequestDTO {
    private String title;
    private String content;
    private Integer pinned;
}
