package com.hwanseung.backend.domain.inquiry.service;

import com.hwanseung.backend.domain.inquiry.dto.InquiryCreateRequestDTO;
import com.hwanseung.backend.domain.inquiry.dto.InquiryResponseDTO;
import com.hwanseung.backend.domain.inquiry.entity.Inquiry;
import com.hwanseung.backend.domain.inquiry.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    public List<InquiryResponseDTO> list() {
        return inquiryRepository.findAll()
                .stream().map(this::toDTO).toList();
    }

    public void create(InquiryCreateRequestDTO dto) {
        inquiryRepository.save(Inquiry.builder()
                .question(dto.getQuestion())
                .userId(dto.getUserId())
                .answered(false)
                .createdAt(LocalDateTime.now())
                .build());
    }

    public void answer(Long id, String answer) {
        Inquiry i = inquiryRepository.findById(id).orElseThrow();
        i = Inquiry.builder()
                .id(i.getId())
                .question(i.getQuestion())
                .userId(i.getUserId())
                .answer(answer)
                .answered(true)
                .createdAt(i.getCreatedAt())
                .build();
        inquiryRepository.save(i);
    }

    public void delete(Long id) {
        inquiryRepository.deleteById(id);
    }

    private InquiryResponseDTO toDTO(Inquiry i) {
        return InquiryResponseDTO.builder()
                .id(i.getId())
                .question(i.getQuestion())
                .answer(i.getAnswer())
                .answered(i.isAnswered())
                .build();
    }
}
