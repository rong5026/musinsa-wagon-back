package com.musinsa.wagon.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "wishlists",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_wishlist_user_product",
                    columnNames = {"user_id", "product_id"})
        },
        indexes = {
            @Index(name = "idx_wishlist_user", columnList = "user_id"),
            @Index(name = "idx_wishlist_product", columnList = "product_id")
        })
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wishlist extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Integer targetPrice;

    @Builder.Default
    @Column(nullable = false)
    private Boolean notificationEnabled = true;

    public void updateTargetPrice(Integer targetPrice) {
        this.targetPrice = targetPrice;
    }

    public void toggleNotification(Boolean enabled) {
        this.notificationEnabled = enabled;
    }

    public boolean hasTargetPrice() {
        return this.targetPrice != null && this.targetPrice > 0;
    }

    public boolean isTargetPriceReached(Integer currentPrice) {
        return hasTargetPrice() && currentPrice <= this.targetPrice;
    }
}
