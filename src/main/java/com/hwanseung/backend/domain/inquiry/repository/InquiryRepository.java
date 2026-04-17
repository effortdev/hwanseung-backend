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

    Page<Inquiry> findByCategory(String category, Pageable pageable);

    Page<Inquiry> findAll(Pageable pageable);

    List<Inquiry> findByCategory(String category, Sort sort);
    List<Inquiry> findAll(Sort sort);
}