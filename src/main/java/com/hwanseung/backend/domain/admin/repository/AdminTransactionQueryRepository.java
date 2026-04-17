package com.hwanseung.backend.domain.admin.repository;

import com.hwanseung.backend.domain.admin.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminTransactionQueryRepository extends JpaRepository<Transaction, Integer> {

    @Query(value = """
            SELECT DATE_FORMAT(created_at, :fmt) AS bucket,
                   COUNT(*)                      AS cnt,
                   COALESCE(SUM(price), 0)       AS amt
              FROM product
             WHERE sale_status = 'SOLD_OUT'
               AND deleted_at IS NULL
               AND created_at >= :from AND created_at < :to
             GROUP BY bucket
             ORDER BY bucket
            """, nativeQuery = true)
    List<Object[]> findSeries(@Param("fmt") String fmt,
                              @Param("from") LocalDateTime from,
                              @Param("to")   LocalDateTime to);

    @Query(value = """
            SELECT sale_status                AS status,
                   COUNT(*)                   AS cnt,
                   COALESCE(SUM(price), 0)    AS amt
              FROM product
             WHERE deleted_at IS NULL
               AND created_at >= :from AND created_at < :to
             GROUP BY sale_status
            """, nativeQuery = true)
    List<Object[]> findStatusBreakdown(@Param("from") LocalDateTime from,
                                       @Param("to")   LocalDateTime to);

    @Query(value = """
            SELECT category                   AS category_name,
                   COUNT(*)                   AS cnt,
                   COALESCE(SUM(price), 0)    AS amt
              FROM product
             WHERE sale_status = 'SOLD_OUT'
               AND deleted_at IS NULL
               AND created_at >= :from AND created_at < :to
             GROUP BY category
             ORDER BY cnt DESC
             LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> findTopCategories(@Param("from") LocalDateTime from,
                                     @Param("to")   LocalDateTime to,
                                     @Param("limit") int limit);

    @Query(value = """
            SELECT product_id, seller_id, seller_nickname, title, category,
                   price, sale_status, created_at
              FROM product
             WHERE deleted_at IS NULL
               AND created_at >= :from AND created_at < :to
             ORDER BY created_at DESC
             LIMIT :size OFFSET :offset
            """, nativeQuery = true)
    List<Object[]> findList(@Param("from") LocalDateTime from,
                            @Param("to")   LocalDateTime to,
                            @Param("size") int size,
                            @Param("offset") int offset);

    @Query(value = """
            SELECT COUNT(*) FROM product
             WHERE deleted_at IS NULL
               AND created_at >= :from AND created_at < :to
            """, nativeQuery = true)
    long countList(@Param("from") LocalDateTime from,
                   @Param("to")   LocalDateTime to);
}
