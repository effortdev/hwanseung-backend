package com.hwanseung.backend.domain.admin.service;

import com.hwanseung.backend.domain.admin.dto.CategoryDTO.Request;
import com.hwanseung.backend.domain.admin.dto.CategoryDTO.Response;
import com.hwanseung.backend.domain.admin.entity.Category;
import com.hwanseung.backend.domain.admin.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCategoryService {

    private final CategoryRepository categoryRepository;
    // private final ProductRepository productRepository;  // 상품 수 카운트용

    /** 전체 목록 조회 (정렬순) */
    @Transactional(readOnly = true)
    public List<Response> getAllCategories() {
        return categoryRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /** 카테고리 등록 */
    @Transactional
    public Response createCategory(Request request) {
        if (categoryRepository.existsByCategoryKey(request.getKey())) {
            throw new IllegalArgumentException("이미 존재하는 카테고리 키입니다: " + request.getKey());
        }

        int maxOrder = categoryRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .mapToInt(Category::getSortOrder)
                .max()
                .orElse(-1);

        Category category = Category.builder()
                .categoryKey(request.getKey())
                .displayName(request.getDisplayName())
                .emoji(request.getEmoji() != null ? request.getEmoji() : "📦")
                .description(request.getDescription())
                .sortOrder(maxOrder + 1)
                .active(true)
                .build();

        return toResponse(categoryRepository.save(category));
    }

    /** 카테고리 수정 */
    @Transactional
    public Response updateCategory(Long id, Request request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("카테고리를 찾을 수 없습니다."));

        category.setDisplayName(request.getDisplayName());
        if (request.getEmoji() != null) category.setEmoji(request.getEmoji());
        if (request.getDescription() != null) category.setDescription(request.getDescription());

        return toResponse(categoryRepository.save(category));
    }

    /** 카테고리 삭제 */
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NoSuchElementException("카테고리를 찾을 수 없습니다.");
        }
        // TODO: 해당 카테고리에 속한 상품 처리 (카테고리 초기화 등)
        // productRepository.clearCategoryByCategoryId(id);
        categoryRepository.deleteById(id);
    }

    /** 순서 변경 */
    @Transactional
    public void updateOrder(List<Long> orderedIds) {
        for (int i = 0; i < orderedIds.size(); i++) {
            Category category = categoryRepository.findById(orderedIds.get(i))
                    .orElseThrow(() -> new NoSuchElementException("카테고리를 찾을 수 없습니다."));
            category.setSortOrder(i);
            categoryRepository.save(category);
        }
    }

    /** 활성/비활성 토글 */
    @Transactional
    public void toggleActive(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("카테고리를 찾을 수 없습니다."));
        category.setActive(!category.getActive());
        categoryRepository.save(category);
    }

    // --- 변환 ---

    private Response toResponse(Category c) {
        // TODO: 실제 상품 수 카운트
        // long productCount = productRepository.countByCategory(c.getCategoryKey());
        long productCount = 0;

        return Response.builder()
                .id(c.getId())
                .key(c.getCategoryKey())
                .displayName(c.getDisplayName())
                .emoji(c.getEmoji())
                .description(c.getDescription())
                .active(c.getActive())
                .sortOrder(c.getSortOrder())
                .productCount(productCount)
                .build();
    }
}
