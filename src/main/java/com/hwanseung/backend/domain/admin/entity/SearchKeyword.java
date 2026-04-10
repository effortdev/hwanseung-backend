package com.hwanseung.backend.domain.admin.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "search_keywords")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String keyword;

    @Column(nullable = false)
    @Builder.Default
    private Long count = 0L;
}
