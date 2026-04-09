package com.hwanseung.backend.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId; // 상품 고유번호

    @Column(name = "title", nullable = false, length = 100)
    private String title; // 상품명

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "price", nullable = false)
    private int price; // 가격

    // 🌟 새로 추가하는 위도/경도 컬럼
    @Column(name = "lat")
    private Double lat;

    @Column(name = "lng")
    private Double lng;


    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content; // 상품 설명 (TEXT로 저장)

    @Column(name = "seller_id", nullable = false, length = 50)
    private String sellerId; // 판매자 아이디

    @Column(name = "seller_nickname", nullable = false, length = 50)
    private String sellerNickname; // 판매자 닉네임

    @Column(name = "location", nullable = false, length = 100)
    private String location; // 거래 지역

    @Builder.Default
    @Column(name = "sale_status", nullable = false, length = 20)
    private String saleStatus = "SALE"; // 판매상태 SALE / SOLD_OUT / RESERVED

    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private int viewCount = 0; // 조회수

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정일

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // 삭제일

    // 🌟 [여기 추가!] DB의 view_count 컬럼과 매핑하고 기본값을 0으로 설정
    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;


    // 상품 1개 : 이미지 여러 개
    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> productImages = new ArrayList<>();

    // 수정
    public void updateProduct(String title, String category, int price, String content, String location) {
        this.title = title;
        this.category = category;
        this.price = price;
        this.content = content;
        this.location = location;
        this.lat = lat;
        this.lng = lng;
    }

    // 소프트 삭제
    public void deleteProduct() {
        this.deletedAt = LocalDateTime.now();
    }

    // 삭제 여부 확인
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    // 판매완료 처리
    public void markAsSoldOut() {
        this.saleStatus = "SOLD_OUT";
    }

    // 판매완료 여부
    public boolean isSoldOut() {
        return "SOLD_OUT".equals(this.saleStatus);
    }

    // 예약중 처리
    public void markAsReserved() {
        this.saleStatus = "RESERVED";
    }

    // 예약중 해제 -> 다시 판매중
    public void markAsSale() {
        this.saleStatus = "SALE";
    }

    // 예약중 여부
    public boolean isReserved() {
        return "RESERVED".equals(this.saleStatus);
    }

    // 조회수 증가
    public void increaseViewCount() {
        this.viewCount++;
    }

    // 양방향 연관관계 편의 메서드
    public void addProductImage(ProductImage productImage) {
        this.productImages.add(productImage);
        productImage.setProduct(this);
    }

    // 기존 이미지 제거
    public void removeProductImage(ProductImage productImage) {
        this.productImages.remove(productImage);
        productImage.setProduct(null);
    }

}