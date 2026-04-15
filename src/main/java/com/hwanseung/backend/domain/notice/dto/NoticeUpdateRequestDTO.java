package com.hwanseung.backend.domain.notice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeUpdateRequestDTO {

    private String title;

    private String content;
}
