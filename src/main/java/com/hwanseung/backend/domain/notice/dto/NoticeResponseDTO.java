package com.hwanseung.backend.domain.notice.dto;

import com.hwanseung.backend.domain.notice.entity.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder    // 🔥 반드시 추가
@NoArgsConstructor
@AllArgsConstructor
public class NoticeResponseDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private Integer pinned;

    public NoticeResponseDTO(Notice notice) {
        this.id = notice.getId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.createdAt = notice.getCreatedAt();
        this.pinned = notice.getPinned();
    }
}