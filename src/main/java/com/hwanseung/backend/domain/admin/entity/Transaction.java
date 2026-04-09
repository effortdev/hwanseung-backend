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

/**
 * 거래(Transaction) 엔티티.
 * 별도의 transactions 테이블이 없기 때문에, Product 테이블을 거래 관점으로
 * 읽기 전용(@Immutable) 매핑하여 관리자 통계/목록 조회에 사용한다.
 *
 *  - sale_status = 'SOLD_OUT' 인 상품을 "완료된 거래"로 간주한다.
 *  - price 를 거래 금액(amount)으로 사용한다.
 *
 * Product 엔티티는 수정하지 않는다. (같은 테이블을 다른 관점으로 매핑)
 */
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

    /** 완료된 거래인지 여부 */
    public boolean isCompleted() {
        return "SOLD_OUT".equals(this.saleStatus);
    }
}
