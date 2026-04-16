package com.hwanseung.backend.domain.search.service;

import com.hwanseung.backend.domain.admin.entity.SearchKeyword;
import com.hwanseung.backend.domain.admin.repository.SearchKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchKeywordRepository searchKeywordRepository;

    @Transactional(readOnly = true)
    public List<SearchKeyword> getPopularKeywords() {
        List<SearchKeyword> top10 = searchKeywordRepository.findTop10ByOrderByCountDesc();
        return top10.size() > 5 ? top10.subList(0, 5) : top10;
    }

    @Transactional
    public void logKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) return;
        String trimmed = keyword.trim();
        if (trimmed.length() > 100) {
            trimmed = trimmed.substring(0, 100);
        }

        SearchKeyword existing = searchKeywordRepository.findByKeyword(trimmed).orElse(null);
        if (existing != null) {
            existing.setCount(existing.getCount() + 1);
        } else {
            searchKeywordRepository.save(SearchKeyword.builder()
                    .keyword(trimmed)
                    .count(1L)
                    .build());
        }
    }
}
