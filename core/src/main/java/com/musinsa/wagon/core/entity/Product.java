package com.musinsa.wagon.core.entity;

import com.musinsa.wagon.core.entity.enums.PriceLabel;
import com.musinsa.wagon.core.entity.enums.ShopType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(
        name = "products",
        indexes = {
            @Index(name = "idx_product_number", columnList = "productNumber", unique = true),
            @Index(name = "idx_product_shop_type", columnList = "shopType"),
            @Index(name = "idx_product_price_label", columnList = "priceLabel"),
            @Index(name = "idx_product_fake_discount", columnList = "isFakeDiscount"),
            @Index(name = "idx_product_category", columnList = "category_id")
        })
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseAuditEntity {

    @Column(length = 512)
    private String imgUrl;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, unique = true)
    private Long productNumber;

    @Column(length = 100)
    private String brand;

    @Column(nullable = false)
    private Integer currentPrice;

    private Integer originalPrice;

    private Integer discountRate;

    @Column(precision = 3, scale = 2)
    private BigDecimal starScore;

    private Integer reviewCount;

    private Integer likeCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ShopType shopType;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PriceLabel priceLabel = PriceLabel.NORMAL;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isFakeDiscount = false;

    private Integer fakeDiscountScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_detail_id")
    private ProductDetail productDetail;

    public void updatePrice(Integer currentPrice, Integer originalPrice, Integer discountRate) {
        this.currentPrice = currentPrice;
        this.originalPrice = originalPrice;
        this.discountRate = discountRate;
    }

    public void updatePriceLabel(PriceLabel priceLabel) {
        this.priceLabel = priceLabel;
    }

    public void updateFakeDiscount(Boolean isFakeDiscount, Integer fakeDiscountScore) {
        this.isFakeDiscount = isFakeDiscount;
        this.fakeDiscountScore = fakeDiscountScore;
    }

    public void updateReviewInfo(BigDecimal starScore, Integer reviewCount) {
        this.starScore = starScore;
        this.reviewCount = reviewCount;
    }

    public void updateLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public void updateCategory(Category category) {
        this.category = category;
    }

    public void updateProductDetail(ProductDetail productDetail) {
        this.productDetail = productDetail;
    }
}
