package com.musinsa.wagon.core.entity;

import com.musinsa.wagon.core.entity.enums.RequestStatus;
import com.musinsa.wagon.core.entity.enums.ShopType;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_product_crawl_requests",
        indexes = {
            @Index(name = "idx_crawl_request_user", columnList = "user_id"),
            @Index(name = "idx_crawl_request_status", columnList = "status")
        })
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProductCrawlRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 512)
    private String productUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ShopType shopType;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RequestStatus status = RequestStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(length = 1000)
    private String errorMessage;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    private LocalDateTime processedAt;

    public void startProcessing() {
        this.status = RequestStatus.PROCESSING;
    }

    public void complete(Product product) {
        this.status = RequestStatus.COMPLETED;
        this.product = product;
        this.processedAt = LocalDateTime.now();
    }

    public void fail(String errorMessage) {
        this.status = RequestStatus.FAILED;
        this.errorMessage = errorMessage;
        this.processedAt = LocalDateTime.now();
    }

    public static UserProductCrawlRequest create(User user, String productUrl, ShopType shopType) {
        return UserProductCrawlRequest.builder()
                .user(user)
                .productUrl(productUrl)
                .shopType(shopType)
                .status(RequestStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();
    }
}
