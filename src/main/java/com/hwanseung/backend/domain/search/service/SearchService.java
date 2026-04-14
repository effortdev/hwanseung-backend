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

    /**
     * 인기 검색어 Top 5 조회
     * - 기존 findTop10 재활용 후 5개만 반환
     */
    @Transactional(readOnly = true)
    public List<SearchKeyword> getPopularKeywords() {
        List<SearchKeyword> top10 = searchKeywordRepository.findTop10ByOrderByCountDesc();
        return top10.size() > 5 ? top10.subList(0, 5) : top10;
    }

    /**
     * 검색 시 키워드 카운트 증가 (없으면 신규 생성)
     */
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
