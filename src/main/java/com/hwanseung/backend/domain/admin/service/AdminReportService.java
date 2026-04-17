package com.hwanseung.backend.domain.admin.service;

import com.hwanseung.backend.domain.admin.dto.ReportDTO.*;
import com.hwanseung.backend.domain.admin.dto.Status;
import com.hwanseung.backend.domain.admin.entity.Report;
import com.hwanseung.backend.domain.admin.entity.ReportHistory;
import com.hwanseung.backend.domain.admin.repository.AdminReportRepository;
import com.hwanseung.backend.domain.product.entity.Product;
import com.hwanseung.backend.domain.product.repository.ProductRepository;
import com.hwanseung.backend.domain.user.entity.User;
import com.hwanseung.backend.domain.user.repository.UserRepository;
import com.hwanseung.backend.domain.user.service.TrustScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminReportService {

    private final AdminReportRepository reportRepository;
    private final UserRepository userRepository;
     private final ProductRepository productRepository;
    // 👇 TrustScoreService 의존성 추가
    private final TrustScoreService trustScoreService;

    @Transactional(readOnly = true)
    public Map<String, Object> getReports(int page, int size, String keyword, String status, String type) {
        Page<Report> result = reportRepository.searchReports(
                keyword != null ? keyword : "",
                status != null ? status : "",
                type != null ? type : "",
                PageRequest.of(page, size));

        Page<ListResponse> dtoPage = result.map(this::toListResponse);

        SummaryCount summary = SummaryCount.builder()
                .total(reportRepository.count())
                .pending(reportRepository.countByStatus("PENDING"))
                .warned(reportRepository.countByStatus("WARNED"))
                .suspended(reportRepository.countByStatus("SUSPENDED"))
                .dismissed(reportRepository.countByStatus("DISMISSED"))
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("content", dtoPage.getContent());
        response.put("totalPages", dtoPage.getTotalPages());
        response.put("summary", summary);
        return response;
    }

    @Transactional(readOnly = true)
    public DetailResponse getReportDetail(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new NoSuchElementException("신고를 찾을 수 없습니다. ID: " + reportId));
        return toDetailResponse(report);
    }

    @Transactional
    public void warnUser(Long reportId, String memo, String adminNickname) {
        Report report = findReport(reportId);
        report.setStatus("WARNED");
        addHistory(report, "WARNED", memo, adminNickname);
        reportRepository.save(report);

    }

    @Transactional
    public void suspendUser(Long reportId, int days, String memo, String adminNickname) {
        Report report = findReport(reportId);
        report.setStatus("SUSPENDED");
        addHistory(report, "SUSPENDED", memo, adminNickname);
        reportRepository.save(report);

         User user = userRepository.findById(report.getReportedUserId())
                 .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
         user.setStatus(Status.valueOf("SUSPENDED"));
         user.setSuspendedAt(LocalDateTime.now());
         user.setSuspendUntil(days == 0 ? null : LocalDateTime.now().plusDays(days));
         userRepository.save(user);

        // 👇 [추가된 로직] 계정 정지 처분 시 피신고자 신뢰도 대폭 차감 (-50점 예시)
        trustScoreService.updateTrustScore(report.getReportedUserId(), -50, "서비스 규정 위반으로 인한 계정 정지");
    }

    @Transactional
    public void dismissReport(Long reportId, String memo, String adminNickname) {
        Report report = findReport(reportId);
        report.setStatus("DISMISSED");
        addHistory(report, "DISMISSED", memo, adminNickname);
        reportRepository.save(report);

        // 👇 [추가된 로직] 관리자가 기각(무혐의/허위신고) 처리 시 신고자에게 페널티 차감 (-10점 예시)
        trustScoreService.updateTrustScore(report.getReporterId(), -10, "허위/오인 신고로 인한 신뢰도 하락");
    }

    @Transactional
    public void deleteReportedContent(Long reportId) {
        Report report = findReport(reportId);
        if ("PRODUCT".equals(report.getType()) && report.getTargetProductId() != null) {
        }
        report.setStatus("RESOLVED");
        reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getSuspendedUsers(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<User> userPage = userRepository.findSuspendedUsers(keyword, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("content", userPage.getContent());
        response.put("totalPages", userPage.getTotalPages());
        response.put("totalElements", userPage.getTotalElements());
        return response;
    }

    private Report findReport(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new NoSuchElementException("신고를 찾을 수 없습니다."));
    }

    private void addHistory(Report report, String action, String memo, String adminNickname) {
        ReportHistory history = ReportHistory.builder()
                .report(report)
                .action(action)
                .memo(memo)
                .adminNickname(adminNickname)
                .build();
        report.getHistory().add(history);
    }


    private ListResponse toListResponse(Report r) {
        return ListResponse.builder()
                .id(r.getId())
                .type(r.getType())
                .status(r.getStatus())
                .reasonCategory(r.getReasonCategory())
                .reason(r.getReason())
                .reporterNickname(r.getReporterNickname())
                .reportedNickname(r.getReportedNickname())
                .createdAt(r.getCreatedAt())
                .build();
    }

    private DetailResponse toDetailResponse(Report r) {
        int reportedCount = (int) reportRepository.countByReportedUserId(r.getReportedUserId());

        TargetProductInfo productInfo = null;
        if ("PRODUCT".equals(r.getType()) && r.getTargetProductId() != null) {
             Product product = productRepository.findById(Math.toIntExact(r.getTargetProductId())).orElse(null);
             if (product != null) {
                 productInfo = new TargetProductInfo(product.getProductId().longValue(), product.getTitle(), product.getPrice());
             }
        }

        List<HistoryItem> history = r.getHistory().stream()
                .map(h -> new HistoryItem(h.getAction(), h.getMemo(), h.getAdminNickname(), h.getCreatedAt()))
                .collect(Collectors.toList());

        return DetailResponse.builder()
                .id(r.getId())
                .type(r.getType())
                .status(r.getStatus())
                .reasonCategory(r.getReasonCategory())
                .reason(r.getReason())
                .reporterNickname(r.getReporterNickname())
                .reporterEmail(r.getReporterEmail())
                .reportedNickname(r.getReportedNickname())
                .reportedEmail(r.getReportedEmail())
                .reportedReportCount(reportedCount)
                .targetProduct(productInfo)
                .history(history)
                .createdAt(r.getCreatedAt())
                .build();
    }
}
