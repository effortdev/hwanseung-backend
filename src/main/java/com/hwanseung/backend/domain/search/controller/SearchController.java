package com.hwanseung.backend.domain.search.controller;

import com.hwanseung.backend.domain.admin.entity.SearchKeyword;
import com.hwanseung.backend.domain.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/popular")
    public ResponseEntity<List<Map<String, Object>>> getPopularKeywords() {
        List<SearchKeyword> keywords = searchService.getPopularKeywords();

        List<Map<String, Object>> result = keywords.stream()
                .map(k -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("keyword", k.getKeyword());
                    map.put("count", k.getCount());
                    return map;
                })
                .toList();

        return ResponseEntity.ok(result);
    }

    @PostMapping("/log")
    public ResponseEntity<Void> logKeyword(@RequestBody Map<String, String> body) {
        searchService.logKeyword(body.get("keyword"));
        return ResponseEntity.ok().build();
    }
}
