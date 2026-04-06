package com.hwanseung.backend.domain.product.repository;

import com.hwanseung.backend.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    // 삭제되지 않은 상품만 최신순 조회
    List<Product> findByDeletedAtIsNullOrderByCreatedAtDesc();

    // 1. 전체 상품 개수 (기본 제공)
    long count();

    // deleted_at 컬럼이 null인 데이터만 카운트
    long countByDeletedAtIsNull();

    // 판매중 먼저, 판매완료 아래, 그 안에서 최신순
    @Query("""
        select p
        from Product p
        where p.deletedAt is null
        order by
            case when p.saleStatus = 'SALE' then 0 else 1 end,
            p.createdAt desc
    """)
    List<Product> findAllVisibleOrderBySaleStatusAndCreatedAtDesc();

    // 삭제되지 않은 상품 단건 조회
    Optional<Product> findByProductIdAndDeletedAtIsNull(Integer productId);
}