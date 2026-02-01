package com.musinsa.wagon.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(
        name = "product_histories",
        indexes = {
            @Index(
                    name = "idx_product_history_product_date",
                    columnList = "product_id, createdAt DESC")
        })
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer price;

    private Integer originalPrice;

    private Integer discountRate;

    @Column(nullable = false)
    private LocalDate createdAt;

    public static ProductHistory createHistory(
            Product product, Integer price, Integer originalPrice, Integer discountRate) {
        return ProductHistory.builder()
                .product(product)
                .price(price)
                .originalPrice(originalPrice)
                .discountRate(discountRate)
                .createdAt(LocalDate.now())
                .build();
    }
}
