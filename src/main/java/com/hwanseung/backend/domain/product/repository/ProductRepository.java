package com.hwanseung.backend.domain.product.repository;

import com.hwanseung.backend.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    // 삭제되지 않은 상품만 최신순 조회
    List<Product> findByDeletedAtIsNullOrderByCreatedAtDesc();

    // 1. 전체 상품 개수 (기본 제공)
    long count();

    // deleted_at 컬럼이 null인 데이터만 카운트
    long countByDeletedAtIsNull();
}