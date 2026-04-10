package com.hwanseung.backend.domain.admin.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 카테고리 고유 키 (예: digital, fashion) */
    @Column(nullable = false, unique = true, length = 50)
    private String categoryKey;

    /** 화면 표시 이름 (예: 디지털기기) */
    @Column(nullable = false, length = 50)
    private String displayName;

    /** 이모지 아이콘 */
    @Column(length = 10)
    @Builder.Default
    private String emoji = "📦";

    /** 설명 */
    @Column(length = 200)
    private String description;

    /** 정렬 순서 (낮을수록 앞에 표시) */
    @Column(nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    /** 활성/비활성 */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
