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
        List<Inquiry> list = null;
        if(!category.equals("all")){
            list = inquiryRepository.findByCategory(category, Sort.by("createdAt").descending());
        }else{
            list = inquiryRepository.findAll(Sort.by("createdAt").descending());
        }
        return list;
    }

    public Page<Inquiry> getInquiries(String category, int page) {
        Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "id"));

        if (category == null || category.equals("all")) {
            return inquiryRepository.findAll(pageable);
        }
        return inquiryRepository.findByCategory(category, pageable);
    }

    @Transactional
    public Inquiry save(Inquiry inquiry) {
        return inquiryRepository.save(inquiry);
    }

    @Transactional
    public Inquiry update(Long id, Inquiry requestDto) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 질문이 존재하지 않습니다. id=" + id));

        inquiry.setCategory(requestDto.getCategory());
        inquiry.setQuestion(requestDto.getQuestion());
        inquiry.setAnswer(requestDto.getAnswer());

        return inquiry;
    }

    @Transactional
    public void delete(Long id) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 질문이 존재하지 않습니다. id=" + id));

        inquiryRepository.delete(inquiry);
    }
}