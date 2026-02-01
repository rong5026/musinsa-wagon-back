package com.musinsa.wagon.core.entity;

import com.musinsa.wagon.core.entity.enums.FakeDiscountPattern;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "fake_discount_histories",
        indexes = {
            @Index(name = "idx_fake_discount_product", columnList = "product_id"),
            @Index(name = "idx_fake_discount_holiday", columnList = "holiday_id"),
            @Index(name = "idx_fake_discount_detected_at", columnList = "detectedAt")
        })
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FakeDiscountHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holiday_id")
    private Holiday holiday;

    @Column(nullable = false)
    private LocalDateTime detectedAt;

    private Integer priceBeforeRaise;

    private Integer raisedPrice;

    private Integer discountedPrice;

    private Integer fakeDiscountRate;

    private Integer realDiscountRate;

    @Column(nullable = false)
    private Integer confidenceScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private FakeDiscountPattern patternType;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public static FakeDiscountHistory create(
            Product product,
            Holiday holiday,
            Integer priceBeforeRaise,
            Integer raisedPrice,
            Integer discountedPrice,
            Integer confidenceScore,
            FakeDiscountPattern patternType) {

        int fakeDiscount =
                raisedPrice > 0 ? (int) ((raisedPrice - discountedPrice) * 100.0 / raisedPrice) : 0;
        int realDiscount =
                priceBeforeRaise > 0
                        ? (int) ((priceBeforeRaise - discountedPrice) * 100.0 / priceBeforeRaise)
                        : 0;

        return FakeDiscountHistory.builder()
                .product(product)
                .holiday(holiday)
                .detectedAt(LocalDateTime.now())
                .priceBeforeRaise(priceBeforeRaise)
                .raisedPrice(raisedPrice)
                .discountedPrice(discountedPrice)
                .fakeDiscountRate(fakeDiscount)
                .realDiscountRate(realDiscount)
                .confidenceScore(confidenceScore)
                .patternType(patternType)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
