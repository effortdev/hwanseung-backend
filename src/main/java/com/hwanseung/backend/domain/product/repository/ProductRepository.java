package com.hwanseung.backend.domain.product.repository;

import com.hwanseung.backend.domain.product.dto.ProductListResponseDTO;
import com.hwanseung.backend.domain.product.entity.Product;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByDeletedAtIsNullOrderByCreatedAtDesc();

    List<Product> findBySellerIdOrderByCreatedAtDesc(String sellerId);

    List<Product> findBySellerIdAndDeletedAtIsNullOrderByCreatedAtDesc(String sellerId);

    List<Product> findByBuyerUsernameOrderByPayStatusAscCreatedAtDesc(String buyerId);

    // 1. 전체 상품 개수 (기본 제공)
    long count();

    long countByDeletedAtIsNull();
    long countByCreatedAtAfter(LocalDateTime dateTime);
    long countByPriceLessThanEqual(int price);
    long countByPriceBetween(int min, int max);
    long countByPriceGreaterThan(int price);
    long countByCategory(String category);

    @Query("SELECT p.category, COUNT(p) FROM Product p GROUP BY p.category ORDER BY COUNT(p) DESC")
    List<Object[]> countByCategory();

    @Query("SELECT p FROM Product p WHERE " +
            "p.deletedAt IS NULL " +
            "AND (:keyword IS NULL OR p.title LIKE %:keyword% OR p.location LIKE %:keyword%) " +
            "AND (:status IS NULL OR p.saleStatus = :status) " +
            "AND (:category IS NULL OR p.category = :category)")
    Page<Product> searchProducts(@Param("keyword") String keyword,
                                 @Param("status") String status,
                                 @Param("category") String category,
                                 Pageable pageable);

    @Query("""
        select p
        from Product p
        where p.deletedAt is null
        order by
            case when p.saleStatus = 'SALE' then 0 else 1 end,
            p.createdAt desc
    """)
    List<Product> findAllVisibleOrderBySaleStatusAndCreatedAtDesc();

    Optional<Product> findByProductIdAndDeletedAtIsNull(Integer productId);

    @Query("""
        select p
        from Product p
        where p.deletedAt is null
          and p.saleStatus = 'SALE'
        order by p.createdAt desc
    """)
    List<Product> findAllVisibleSaleProductsOrderByCreatedAtDesc();


    @Query(value = """
        SELECT * FROM product p
        WHERE p.deleted_at IS NULL
        AND p.sale_status = 'SALE'
        AND (
            6371 * acos(
                cos(radians(:userLat)) 
                * cos(radians(p.lat)) 
                * cos(radians(p.lng) - radians(:userLng)) 
                + sin(radians(:userLat)) 
                * sin(radians(p.lat))
            )
        ) <= :radius
        ORDER BY p.created_at DESC
    """, nativeQuery = true)
    List<Product> findNearbyProducts(@Param("userLat") double userLat,
                                     @Param("userLng") double userLng,
                                     @Param("radius") double radius);

    long countBySaleStatusAndDeletedAtIsNull(String sale);


}