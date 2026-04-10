package com.hwanseung.backend.domain.admin.repository;

import com.hwanseung.backend.domain.admin.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 관리자 거래 통계/목록 조회 Repository.
 *
 * 별도의 transactions 테이블이 존재하지 않으므로, Product 테이블(product)을
 * 거래(Transaction) 관점으로 네이티브 쿼리를 이용해 집계한다.
 *
 *  - "완료된 거래" 기준: sale_status = 'SOLD_OUT'
 *  - 금액(amount) 기준:  price 컬럼
 *  - 삭제된 상품(deleted_at IS NOT NULL)은 집계에서 제외
 */
public interface AdminTransactionQueryRepository extends JpaRepository<Transaction, Integer> {

    /** 일/주/월 버킷 단위 거래 시계열 (완료된 거래 기준) */
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

    /** 판매 상태별 분포 (SALE / SOLD_OUT 등) */
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

    /** 카테고리별 거래 TOP N (완료된 거래 기준) */
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

    /** 거래 내역 페이지 (기간 내 상품 전체) */
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

    /** 거래 내역 총 개수 */
    @Query(value = """
            SELECT COUNT(*) FROM product
             WHERE deleted_at IS NULL
               AND created_at >= :from AND created_at < :to
            """, nativeQuery = true)
    long countList(@Param("from") LocalDateTime from,
                   @Param("to")   LocalDateTime to);
}
