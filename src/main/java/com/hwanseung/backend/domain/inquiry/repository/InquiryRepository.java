package com.hwanseung.backend.domain.inquiry.repository;

import com.hwanseung.backend.domain.inquiry.entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    // 🔍 전체 조회 (질문 검색)
    Page<Inquiry> findByQuestionContaining(String keyword, Pageable pageable);

    // 👤 특정 유저의 문의 조회
    Page<Inquiry> findByUserId(Long userId, Pageable pageable);

    // ❗ 답변 여부 필터
    Page<Inquiry> findByAnswered(boolean answered, Pageable pageable);

    // 🔍 유저 + 검색
    Page<Inquiry> findByUserIdAndQuestionContaining(Long userId, String keyword, Pageable pageable);

    // 🔍 답변 여부 + 검색
    Page<Inquiry> findByAnsweredAndQuestionContaining(boolean answered, String keyword, Pageable pageable);
}
