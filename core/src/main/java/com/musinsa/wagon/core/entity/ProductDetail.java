package com.musinsa.wagon.core.entity;

import com.musinsa.wagon.core.entity.enums.CrawlStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "product_details",
        indexes = {@Index(name = "idx_product_detail_url", columnList = "productUrl", unique = true)})
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductDetail extends BaseEntity {

    @Column(nullable = false, unique = true, length = 512)
    private String productUrl;

    private Integer highPrice30;

    private Integer lowPrice30;

    private Integer avgPrice30;

    private Integer highPrice90;

    private Integer lowPrice90;

    private Integer avgPrice90;

    private Integer allTimeHighPrice;

    private Integer allTimeLowPrice;

    private LocalDateTime lastCrawledAt;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CrawlStatus crawlStatus = CrawlStatus.PENDING;

    public void updatePriceStatistics30(Integer highPrice, Integer lowPrice, Integer avgPrice) {
        this.highPrice30 = highPrice;
        this.lowPrice30 = lowPrice;
        this.avgPrice30 = avgPrice;
    }

    public void updatePriceStatistics90(Integer highPrice, Integer lowPrice, Integer avgPrice) {
        this.highPrice90 = highPrice;
        this.lowPrice90 = lowPrice;
        this.avgPrice90 = avgPrice;
    }

    public void updateAllTimePrices(Integer highPrice, Integer lowPrice) {
        this.allTimeHighPrice = highPrice;
        this.allTimeLowPrice = lowPrice;
    }

    public void updateCrawlStatus(CrawlStatus status) {
        this.crawlStatus = status;
        this.lastCrawledAt = LocalDateTime.now();
    }
}
