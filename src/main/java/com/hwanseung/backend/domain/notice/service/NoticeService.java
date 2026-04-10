package com.hwanseung.backend.domain.notice.service;

import com.hwanseung.backend.domain.notice.dto.NoticeCreateRequestDTO;
import com.hwanseung.backend.domain.notice.dto.NoticeResponseDTO;
import com.hwanseung.backend.domain.notice.dto.NoticeUpdateRequestDTO;
import com.hwanseung.backend.domain.notice.entity.Notice;
import com.hwanseung.backend.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public Page<NoticeResponseDTO> getList(String keyword, Pageable pageable) {
        return noticeRepository.findByTitleContainingOrderByPinnedDescCreatedAtDesc(keyword, pageable)
                .map(this::toDTO);
    }

    public NoticeResponseDTO get(Long id) {
        return toDTO(noticeRepository.findById(id).orElseThrow());
    }

    public void create(NoticeCreateRequestDTO dto) {
        noticeRepository.save(Notice.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .pinned(dto.getPinned())
                .build());
    }

    public void update(Long id, NoticeUpdateRequestDTO dto) {
        Notice notice = noticeRepository.findById(id).orElseThrow();
        notice = Notice.builder()
                .id(id)
                .title(dto.getTitle())
                .content(dto.getContent())
                .pinned(notice.getPinned())
                .createdAt(notice.getCreatedAt())
                .build();
        noticeRepository.save(notice);
    }

    public void delete(Long id) {
        noticeRepository.deleteById(id);
    }

    private NoticeResponseDTO toDTO(Notice n) {
        return NoticeResponseDTO.builder()
                .id(n.getId())
                .title(n.getTitle())
                .content(n.getContent())
                .createdAt(n.getCreatedAt())
                .pinned(n.getPinned())
                .build();
    }
}
