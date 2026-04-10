package com.hwanseung.backend.domain.report.service;

import com.hwanseung.backend.domain.admin.entity.Report;
import com.hwanseung.backend.domain.product.entity.Product;
import com.hwanseung.backend.domain.product.repository.ProductRepository;
import com.hwanseung.backend.domain.report.dto.ReportCheckResponseDTO;   // ✅추가
import com.hwanseung.backend.domain.report.dto.ReportCreateRequestDTO;
import com.hwanseung.backend.domain.report.dto.ReportCreateResponseDTO;
import com.hwanseung.backend.domain.report.repository.ReportRepository;
import com.hwanseung.backend.domain.user.config.CustomUserDetails;
import com.hwanseung.backend.domain.user.entity.User;
import com.hwanseung.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private static final Set<String> VALID = Set.of(
            "FRAUD", "SPAM", "ABUSIVE", "INAPPROPRIATE",
            "COUNTERFEIT", "PROHIBITED", "OTHER"
    );

    // 상세페이지 신고 버튼 누를 때 중복 신고 여부 확인
    @Transactional(readOnly = true)
    public ReportCheckResponseDTO checkProductReport(Long productId, Authentication authentication) {

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails loginUser)) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        Product product = productRepository.findById(productId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("상품 없음"));

        User reporter = userRepository.findByUsername(loginUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        // 본인 상품 신고 금지
        if (reporter.getUsername().equals(product.getSellerId())) {
            return ReportCheckResponseDTO.of(true, "본인 상품은 신고할 수 없습니다.");
        }

        boolean reported = reportRepository.existsByTypeAndReporterIdAndTargetProductId(
                "PRODUCT",
                reporter.getId(),
                productId
        );

        if (reported) {
            return ReportCheckResponseDTO.of(true, "이미 신고한 상품입니다.");
        }

        return ReportCheckResponseDTO.of(false, "신고 가능한 상품입니다.");
    }

    public ReportCreateResponseDTO createProductReport(
            Long productId,
            ReportCreateRequestDTO dto,
            Authentication authentication
    ) {

        // 로그인 체크
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails loginUser)) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        // 카테고리 검증
        if (dto.getReasonCategory() == null || dto.getReasonCategory().trim().isEmpty()) {
            throw new IllegalArgumentException("신고 유형을 선택하세요.");
        }

        String category = dto.getReasonCategory().trim().toUpperCase();

        if (!VALID.contains(category)) {
            throw new IllegalArgumentException("잘못된 신고 유형입니다.");
        }

        if (dto.getReason() == null || dto.getReason().trim().isEmpty()) {
            throw new IllegalArgumentException("신고 사유를 입력하세요.");
        }

        // 상품 조회
        Product product = productRepository.findById(productId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("상품 없음"));

        // 신고자
        User reporter = userRepository.findByUsername(loginUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        // 판매자
        User seller = userRepository.findByUsername(product.getSellerId())
                .orElseThrow(() -> new IllegalArgumentException("판매자 없음"));

        // 본인 신고 금지
        if (reporter.getUsername().equals(product.getSellerId())) {
            throw new IllegalArgumentException("본인 상품 신고 불가");
        }

        // 중복 신고 금지
        if (reportRepository.existsByTypeAndReporterIdAndTargetProductId(
                "PRODUCT",
                reporter.getId(),
                productId
        )) {
            throw new IllegalArgumentException("이미 신고한 상품입니다.");
        }

        // 저장
        Report report = Report.builder()
                .type("PRODUCT")
                .status("PENDING")
                .reasonCategory(category)
                .reason(dto.getReason())
                .reporterId(reporter.getId())
                .reporterNickname(reporter.getNickname())
                .reporterEmail(reporter.getEmail())
                .reportedUserId(seller.getId())
                .reportedNickname(seller.getNickname())
                .reportedEmail(seller.getEmail())
                .targetProductId(productId)
                .build();

        Report saved = reportRepository.save(report);

        // 신고 횟수 증가
        Integer currentReportCount = product.getReportCount();
        product.setReportCount(currentReportCount == null ? 1 : currentReportCount + 1);

        return ReportCreateResponseDTO.success(saved.getId());
    }
}