package com.hwanseung.backend.domain.notice.controller;

import com.hwanseung.backend.domain.notice.dto.NoticeCreateRequestDTO;
import com.hwanseung.backend.domain.notice.dto.NoticeResponseDTO;
import com.hwanseung.backend.domain.notice.dto.NoticeUpdateRequestDTO;
import com.hwanseung.backend.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public Page<NoticeResponseDTO> list(String keyword, Pageable pageable) {
        return noticeService.getList(keyword == null ? "" : keyword, pageable);
    }

    @GetMapping("/{id}")
    public NoticeResponseDTO detail(@PathVariable Long id) {
        return noticeService.get(id);
    }

    @PostMapping
    public void create(@RequestBody NoticeCreateRequestDTO dto) {
        noticeService.create(dto);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody NoticeUpdateRequestDTO dto) {
        noticeService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        noticeService.delete(id);
    }
}
