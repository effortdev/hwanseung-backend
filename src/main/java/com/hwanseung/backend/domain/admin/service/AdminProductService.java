package com.hwanseung.backend.domain.admin.service;

import com.hwanseung.backend.domain.admin.dto.AdminProductDTO;
import com.hwanseung.backend.domain.admin.dto.AdminProductDTO.SummaryCount;
import com.hwanseung.backend.domain.admin.entity.Report;
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
        // 빈 문자열을 null로 변환하여 JPA 에러 방지
        String safeKeyword = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
        String safeStatus = (status == null || status.trim().isEmpty()) ? null : status.trim();
        String safeCategory = (category == null || category.trim().isEmpty()) ? null : category.trim();
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
                    // Number를 통해 ID 타입(Integer/Long) 상관없이 안전하게 변환
                    .productId(p.getProductId() != null ? Long.valueOf(p.getProductId().toString()) : null)
                    .title(p.getTitle())
                    .category(p.getCategory())
                    .price(p.getPrice())
                    .location(p.getLocation())
                    .sellerNickname(p.getSellerNickname())
                    .saleStatus(p.getSaleStatus())
                    .reportCount(p.getReportCount() != null ? p.getReportCount() : 0) // Null 방어 로직
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

    /**
     * 상품 상세 정보 조회
     * - 엔티티 직접 반환 시 발생하는 순환 참조 및 지연 로딩 문제를 DTO 변환으로 해결
     * - 프론트엔드(React)에서 요구하는 이미지 리스트 및 신고 이력 포함
     */
    @Transactional(readOnly = true)
    public AdminProductDTO.DetailResponse getProductDetail(Long productId) {
        Product product = productRepository.findById(Math.toIntExact(productId))
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));

        List<AdminProductDTO.ProductImageDTO> imageDTOs = product.getProductImages().stream()
                .map(img -> new AdminProductDTO.ProductImageDTO(
                        img.getProductImageId() != null ? Long.valueOf(img.getProductImageId().toString()) : null,
                        img.getImagePath()
                )).collect(Collectors.toList());

        // 1. Service 단에서 ReportRepository를 사용하여 데이터 조회
        List<Report> reports = reportRepository.findByTargetProductId(product.getProductId().longValue());

        // 2. 조회된 List<Report>를 Stream으로 DTO 변환
        List<AdminProductDTO.ProductReportDTO> reportDTOs = reports.stream()
                .map(report -> new AdminProductDTO.ProductReportDTO(report.getReason(), report.getCreatedAt()))
                .collect(Collectors.toList());

        return AdminProductDTO.DetailResponse.builder()
                .productId(productId)
                .title(product.getTitle())
                .content(product.getContent())
                .category(product.getCategory())
                .price(product.getPrice())
                .location(product.getLocation())
                .sellerNickname(product.getSellerNickname())
                .saleStatus(product.getSaleStatus())
                .productImages(imageDTOs)
                .reportCount(product.getReportCount() != null ? product.getReportCount() : 0)
                .hideReason(product.getHideReason()) // 승인 반려 사유 제외, 숨김 사유만 유지
                .reports(reportDTOs)
                .createdAt(product.getCreatedAt())
                .build();
    }

    /** 상품 숨김 */
    @Transactional
    public void hideProduct(Long productId, String reason) {
        Product product = productRepository.findById(Math.toIntExact(productId))
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));
        product.setSaleStatus("HIDDEN");
        product.setHideReason(reason);
    }

    /** 상품 숨김 해제 -> 기본 판매중(SALE) 상태로 복구 */
    @Transactional
    public void unhideProduct(Long productId) {
        Product product = productRepository.findById(Math.toIntExact(productId))
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));
        product.setSaleStatus("SALE");
        product.setHideReason(null);
    }

    /** 상품 삭제 (Soft Delete 권장 - 프로젝트 요건 반영) */
    @Transactional
    public void deleteProduct(Long productId) {
        productRepository.deleteById(Math.toIntExact(productId));
    }

    @Transactional
    public void bulkDelete(List<Integer> productIds) {
        productRepository.deleteAllByIdInBatch(productIds);
    }
}
