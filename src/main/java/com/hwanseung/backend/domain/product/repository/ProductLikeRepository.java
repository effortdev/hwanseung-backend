package com.hwanseung.backend.domain.product.repository;

import com.hwanseung.backend.domain.product.entity.Product;
import com.hwanseung.backend.domain.product.entity.ProductLike;
import com.hwanseung.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Integer> {

    Optional<ProductLike> findByProductAndUser(Product product, User user);

    boolean existsByProductAndUser(Product product, User user);

    long countByProduct(Product product);

    void deleteByProductAndUser(Product product, User user);

    List<ProductLike> findByUser_Username(String username);
}