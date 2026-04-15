package com.hwanseung.backend.domain.notice.repository;



import com.hwanseung.backend.domain.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findAll(Sort sort);
    Page<Notice> findByTitleContaining(String keyword, Pageable pageable);
    Page<Notice> findByTitleContainingOrderByPinnedDescCreatedAtDesc(String keyword, Pageable pageable);
}
