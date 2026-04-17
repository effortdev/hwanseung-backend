package com.hwanseung.backend.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@Setter
@ToString(exclude = "productImages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lng")
    private Double lng;


    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "seller_id", nullable = false, length = 50)
    private String sellerId;

    @Column(name = "seller_nickname", nullable = false, length = 50)
    private String sellerNickname;

    @Column(name = "location", nullable = false, length = 100)
    private String location;

    @Builder.Default
    @Column(name = "sale_status", nullable = false, length = 20)
    private String saleStatus = "SALE";

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer reportCount = 0;

    @Column(name="buyer_username")
    private String buyerUsername;

    @Column(name="pay_status")
    private Boolean payStatus;

    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> productImages = new ArrayList<>();

    @Column(name = "hide_reason", length = 500)
    private String hideReason;

    public void updateProduct(String title, String category, int price, String content, String location) {
        this.title = title;
        this.category = category;
        this.price = price;
        this.content = content;
        this.location = location;
        this.lat = lat;
        this.lng = lng;
    }

    public void deleteProduct() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public void markAsSoldOut() {
        this.saleStatus = "SOLD_OUT";
    }

    public boolean isSoldOut() {
        return "SOLD_OUT".equals(this.saleStatus);
    }

    public void markAsReserved() {
        this.saleStatus = "RESERVED";
    }

    public void markAsSale() {
        this.saleStatus = "SALE";
    }

    public boolean isReserved() {
        return "RESERVED".equals(this.saleStatus);
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void addProductImage(ProductImage productImage) {
        this.productImages.add(productImage);
        productImage.setProduct(this);
    }

    public void removeProductImage(ProductImage productImage) {
        this.productImages.remove(productImage);
        productImage.setProduct(null);
    }

    public void setReportCount(Integer reportCount) {
        this.reportCount = reportCount;
    }

    public void markAsHidden(String reason) {
        this.saleStatus = "HIDDEN";
        this.hideReason = reason;
    }

    public void restoreToSale() {
        this.saleStatus = "SALE";
        this.hideReason = null;
    }
}