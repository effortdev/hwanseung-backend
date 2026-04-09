package com.hwanseung.backend.domain.admin.repository;

import com.hwanseung.backend.domain.admin.entity.SearchKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchKeywordRepository extends JpaRepository<SearchKeyword, Long> {

    List<SearchKeyword> findTop10ByOrderByCountDesc();
}
