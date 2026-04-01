package com.hwanseung.backend.domain.product.repository;

import com.hwanseung.backend.domain.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
}