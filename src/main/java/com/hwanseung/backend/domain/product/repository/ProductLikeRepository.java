package com.hwanseung.backend.domain.product.repository;

import com.hwanseung.backend.domain.product.entity.Product;
import com.hwanseung.backend.domain.product.entity.ProductLike;
import com.hwanseung.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Integer> {

    // 해당 유저가 해당 상품 찜했는지 조회
    Optional<ProductLike> findByProductAndUser(Product product, User user);

    // 해당 유저가 해당 상품 찜했는지 여부만 확인
    boolean existsByProductAndUser(Product product, User user);

    // 해당 상품 찜 개수
    long countByProduct(Product product);

    // 찜 취소
    void deleteByProductAndUser(Product product, User user);
}