package com.hwanseung.backend.domain.user.vo;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pay_history")
@Data
public class PayHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyNo;

    private String userId;
    private String impUid;
    private String merchantUid;
    private int amount;
    private String type;
    @CreationTimestamp // 🌟 데이터가 INSERT 될 때 현재 시간을 알아서 찍어주는 마법의 어노테이션!
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}