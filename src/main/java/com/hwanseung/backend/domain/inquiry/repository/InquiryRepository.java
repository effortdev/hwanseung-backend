package com.hwanseung.backend.domain.inquiry.repository;

import com.hwanseung.backend.domain.inquiry.entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    // 카테고리가 'all'이 아닐 때 특정 카테고리만 페이징 조회
    Page<Inquiry> findByCategory(String category, Pageable pageable);

    // 전체 조회 (카테고리 필터가 없을 경우)
    Page<Inquiry> findAll(Pageable pageable);

    List<Inquiry> findByCategory(String category, Sort sort);
    List<Inquiry> findAll(Sort sort);
}