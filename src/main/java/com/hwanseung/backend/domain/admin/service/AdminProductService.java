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

     private final ProductRepository productRepository;
     private final ProductImageRepository productImageRepository;
     private final ReportRepository reportRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> getProducts(int page, int size, String keyword,
                                           String status, String category, String sort) {
        String safeKeyword = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
        String safeStatus = (status == null || status.trim().isEmpty()) ? null : status.trim();
        String safeCategory = (category == null || category.trim().isEmpty()) ? null : category.trim();
         Sort sorting = switch (sort) {
             case "oldest" -> Sort.by("createdAt").ascending();
             case "priceAsc" -> Sort.by("price").ascending();
             case "priceDesc" -> Sort.by("price").descending();
             case "reportDesc" -> Sort.by("reportCount").descending();
             default -> Sort.by("createdAt").descending();
         };
         Page<Product> result = productRepository.searchProducts(keyword, status, category, PageRequest.of(page, size, sorting));

        List<AdminProductDTO.ListResponse> dtoList = result.getContent().stream().map(p -> {
            String thumb = (p.getProductImages() != null && !p.getProductImages().isEmpty())
                    ? p.getProductImages().get(0).getImagePath()
                    : null;

            return AdminProductDTO.ListResponse.builder()
                    .productId(p.getProductId() != null ? Long.valueOf(p.getProductId().toString()) : null)
                    .title(p.getTitle())
                    .category(p.getCategory())
                    .price(p.getPrice())
                    .location(p.getLocation())
                    .sellerNickname(p.getSellerNickname())
                    .saleStatus(p.getSaleStatus())
                    .reportCount(p.getReportCount() != null ? p.getReportCount() : 0)
                    .createdAt(p.getCreatedAt())
                    .thumbnailUrl(thumb)
                    .build();
        }).collect(Collectors.toList());

        SummaryCount summary = SummaryCount.builder()
                .total(productRepository.countByDeletedAtIsNull())
                .sale(productRepository.countBySaleStatusAndDeletedAtIsNull("SALE"))
                .soldOut(productRepository.countBySaleStatusAndDeletedAtIsNull("SOLD_OUT"))
                .reserved(productRepository.countBySaleStatusAndDeletedAtIsNull("RESERVED"))
                .hidden(productRepository.countBySaleStatusAndDeletedAtIsNull("HIDDEN"))
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("content", dtoList);
        response.put("totalPages", result.getTotalPages());
        response.put("summary", summary);
        return response;
    }

    @Transactional(readOnly = true)
    public AdminProductDTO.DetailResponse getProductDetail(Long productId) {
        Product product = productRepository.findById(Math.toIntExact(productId))
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));

        List<AdminProductDTO.ProductImageDTO> imageDTOs = product.getProductImages().stream()
                .map(img -> new AdminProductDTO.ProductImageDTO(
                        img.getProductImageId() != null ? Long.valueOf(img.getProductImageId().toString()) : null,
                        img.getImagePath()
                )).collect(Collectors.toList());

        List<Report> reports = reportRepository.findByTargetProductId(product.getProductId().longValue());

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
                .hideReason(product.getHideReason())
                .reports(reportDTOs)
                .createdAt(product.getCreatedAt())
                .build();
    }

    @Transactional
    public void hideProduct(Long productId, String reason) {
        Product product = productRepository.findById(Math.toIntExact(productId))
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));
        product.setSaleStatus("HIDDEN");
        product.setHideReason(reason);
    }

    @Transactional
    public void unhideProduct(Long productId) {
        Product product = productRepository.findById(Math.toIntExact(productId))
                .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다."));
        product.setSaleStatus("SALE");
        product.setHideReason(null);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        productRepository.deleteById(Math.toIntExact(productId));
    }

    @Transactional
    public void bulkDelete(List<Integer> productIds) {
        productRepository.deleteAllByIdInBatch(productIds);
    }
}
