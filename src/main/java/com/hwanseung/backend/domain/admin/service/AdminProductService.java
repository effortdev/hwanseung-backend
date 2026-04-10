package com.hwanseung.backend.domain.admin.service;

import com.hwanseung.backend.domain.admin.dto.AdminProductDTO;
import com.hwanseung.backend.domain.admin.dto.AdminProductDTO.DetailResponse;
import com.hwanseung.backend.domain.admin.dto.AdminProductDTO.SummaryCount;
import com.hwanseung.backend.domain.product.entity.Product;
import com.hwanseung.backend.domain.product.repository.ProductImageRepository;
import com.hwanseung.backend.domain.product.repository.ProductRepository;
import com.hwanseung.backend.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminProductService {

    // ※ 기존 프로젝트의 Product, ProductImage, ProductRepository 등을 주입받습니다.
    //    아래는 인터페이스 구조만 정의합니다.
     private final ProductRepository productRepository;
     private final ProductImageRepository productImageRepository;
     private final ReportRepository reportRepository;

    /**
     * 상품 목록 조회 (검색 + 필터 + 정렬 + 페이지네이션)
     *
     * ※ 실제 구현 시 ProductRepository에 아래와 같은 커스텀 쿼리 메서드가 필요합니다:
     *
     * @Query("SELECT p FROM Product p WHERE " +
     *        "(:keyword = '' OR p.title LIKE %:keyword% OR p.location LIKE %:keyword%) " +
     *        "AND (:status = '' OR p.saleStatus = :status) " +
     *        "AND (:category = '' OR p.category = :category)")
     * Page<Product> searchProducts(@Param("keyword") String keyword,
     *                              @Param("status") String status,
     *                              @Param("category") String category,
     *                              Pageable pageable);
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getProducts(int page, int size, String keyword,
                                           String status, String category, String sort) {
        // TODO: 실제 ProductRepository로 교체
        // Sort 처리
         Sort sorting = switch (sort) {
             case "oldest" -> Sort.by("createdAt").ascending();
             case "priceAsc" -> Sort.by("price").ascending();
             case "priceDesc" -> Sort.by("price").descending();
             case "reportDesc" -> Sort.by("reportCount").descending();
             default -> Sort.by("createdAt").descending();
         };
         Page<Product> result = productRepository.searchProducts(keyword, status, category, PageRequest.of(page, size, sorting));

// 🌟 수정 포인트: Entity -> DTO 변환 (무한 참조 방지 및 thumbnailUrl 세팅)
        List<AdminProductDTO.ListResponse> dtoList = result.getContent().stream().map(p -> {
            String thumb = (p.getProductImages() != null && !p.getProductImages().isEmpty())
                    ? p.getProductImages().get(0).getImagePath() // 이미지 경로 필드명에 맞게 수정 필요
                    : null;

            return AdminProductDTO.ListResponse.builder()
                    .productId(Long.valueOf(p.getProductId()))
                    .title(p.getTitle())
                    .category(p.getCategory())
                    .price(p.getPrice())
                    .location(p.getLocation())
                    .sellerNickname(p.getSellerNickname())
                    .saleStatus(p.getSaleStatus())
                    .reportCount(p.getReportCount())
                    .createdAt(p.getCreatedAt())
                    .thumbnailUrl(thumb)
                    .build();
        }).collect(Collectors.toList());

        // 요약 카운트
        SummaryCount summary = SummaryCount.builder()
                .total(productRepository.countByDeletedAtIsNull()) // 삭제된 항목 제외 권장
                .sale(productRepository.countBySaleStatusAndDeletedAtIsNull("SALE"))
                .soldOut(productRepository.countBySaleStatusAndDeletedAtIsNull("SOLD_OUT"))
                .reserved(productRepository.countBySaleStatusAndDeletedAtIsNull("RESERVED"))
                .hidden(productRepository.countBySaleStatusAndDeletedAtIsNull("HIDDEN"))
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("content", dtoList); // Entity 대신 DTO 리스트 반환
        response.put("totalPages", result.getTotalPages());
        response.put("summary", summary);
        return response;
    }

    /** 상품 승인 */
    @Transactional
    public void approveProduct(Long productId) {
        // Product product = productRepository.findById(productId)
        //         .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));
        // product.setSaleStatus("SALE");
        // product.setRejectReason(null);
        // productRepository.save(product);
    }

    /** 상품 반려 */
    @Transactional
    public void rejectProduct(Long productId, String reason) {
        // Product product = productRepository.findById(productId)
        //         .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));
        // product.setSaleStatus("REJECTED");
        // product.setRejectReason(reason);
        // productRepository.save(product);
    }

    /** 상품 숨김 */
    @Transactional
    public void hideProduct(Long productId, String reason) {
//         Product product = productRepository.findById(Math.toIntExact(productId))
//                 .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));
//         product.setSaleStatus("HIDDEN");
//         product.setHideReason(reason);
//         productRepository.save(product);
    }

    /** 상품 숨김 해제 */
    @Transactional
    public void unhideProduct(Long productId) {
        // Product product = productRepository.findById(productId)
        //         .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));
        // product.setSaleStatus("SALE");
        // product.setHideReason(null);
        // productRepository.save(product);
    }

    /** 상품 삭제 */
    @Transactional
    public void deleteProduct(Long productId) {
        // productRepository.deleteById(productId);
    }

    /** 일괄 승인 */
    @Transactional
    public void bulkApprove(List<Long> productIds) {
        // productIds.forEach(this::approveProduct);
    }

    /** 일괄 삭제 */
    @Transactional
    public void bulkDelete(List<Long> productIds) {
//         productRepository.deleteAllByIdInBatch(productIds);
    }

//    public DetailResponse getProductDetail(Long productId) {
//    }
}
