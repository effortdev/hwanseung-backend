package com.hwanseung.backend.domain.user.dto;

import com.hwanseung.backend.domain.user.entity.TrustScoreHistory;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TrustScoreHistoryDto {
    private Long id;
    private Integer scoreChange;  // 변동된 점수 (예: +10, -50)
    private String reason;        // 변동 사유
    private LocalDateTime createdAt; // 변동 일시

    /**
     * 엔티티를 DTO로 변환하는 정적 팩토리 메서드
     * 이 방식을 사용하면 서비스나 컨트롤러 코드가 훨씬 깔끔해집니다.
     */
    public static TrustScoreHistoryDto fromEntity(TrustScoreHistory history) {
        return TrustScoreHistoryDto.builder()
                .id(history.getId())
                .scoreChange(history.getScoreChange())
                .reason(history.getReason())
                .createdAt(history.getCreatedAt())
                .build();
    }
}
