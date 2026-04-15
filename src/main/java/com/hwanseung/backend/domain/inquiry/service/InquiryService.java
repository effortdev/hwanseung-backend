package com.hwanseung.backend.domain.inquiry.service;

import com.hwanseung.backend.domain.inquiry.entity.Inquiry;
import com.hwanseung.backend.domain.inquiry.repository.InquiryRepository;
import com.hwanseung.backend.domain.notice.dto.NoticeResponseDTO;
import com.hwanseung.backend.domain.notice.entity.Notice;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryService {

    private final InquiryRepository inquiryRepository;



    public List<Inquiry> getInquiriesAll(String category) {
        System.out.println("category: "+category);
        // 최신순으로 정렬 (ID 내림차순)
        List<Inquiry> list = null;
        if(!category.equals("all")){
            list = inquiryRepository.findByCategory(category, Sort.by("createdAt").descending());
        }else{
            list = inquiryRepository.findAll(Sort.by("createdAt").descending());
        }
        return list;
    }

    /**
     * 질문 목록 조회 (페이징 및 카테고리 필터)
     */
    public Page<Inquiry> getInquiries(String category, int page) {
        // 최신순으로 정렬 (ID 내림차순)
        Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "id"));

        if (category == null || category.equals("all")) {
            return inquiryRepository.findAll(pageable);
        }
        return inquiryRepository.findByCategory(category, pageable);
    }

    /**
     * 질문 등록
     */
    @Transactional
    public Inquiry save(Inquiry inquiry) {
        return inquiryRepository.save(inquiry);
    }

    /**
     * 질문 수정
     */
    @Transactional
    public Inquiry update(Long id, Inquiry requestDto) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 질문이 존재하지 않습니다. id=" + id));

        // 변경 감지를 통한 수정
        inquiry.setCategory(requestDto.getCategory());
        inquiry.setQuestion(requestDto.getQuestion());
        inquiry.setAnswer(requestDto.getAnswer());

        return inquiry;
    }

    /**
     * 질문 삭제
     */
    @Transactional
    public void delete(Long id) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 질문이 존재하지 않습니다. id=" + id));

        inquiryRepository.delete(inquiry);
    }
}