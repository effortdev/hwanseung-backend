package com.hwanseung.backend.domain.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

@Entity
@Immutable
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction {

    @Id
    @Column(name = "product_id")
    private Integer id;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "price")
    private int price;

    @Column(name = "seller_id", length = 50)
    private String sellerId;

    @Column(name = "seller_nickname", length = 50)
    private String sellerNickname;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "sale_status", length = 20)
    private String saleStatus;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public boolean isCompleted() {
        return "SOLD_OUT".equals(this.saleStatus);
    }
}
