package com.hwanseung.backend.domain.product.repository;

import com.hwanseung.backend.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    // 삭제되지 않은 상품만 최신순 조회
    List<Product> findByDeletedAtIsNullOrderByCreatedAtDesc();
}