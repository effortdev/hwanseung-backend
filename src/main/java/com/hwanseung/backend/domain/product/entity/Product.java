package com.hwanseung.backend.domain.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content; // 상품 설명 (TEXT로 저장)

    @Column(name = "seller_id", nullable = false, length = 50)
    private String sellerId; // 판매자 아이디

    @Column(name = "location", nullable = false, length = 100)
    private String location; // 거래 지역
}