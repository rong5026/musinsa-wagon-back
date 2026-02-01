package com.musinsa.wagon.core.entity;

import com.musinsa.wagon.core.entity.enums.CrawlJobType;
import com.musinsa.wagon.core.entity.enums.JobStatus;
import com.musinsa.wagon.core.entity.enums.ShopType;

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
        name = "crawl_jobs",
        indexes = {
            @Index(name = "idx_crawl_job_status", columnList = "status"),
            @Index(name = "idx_crawl_job_started_at", columnList = "startedAt")
        })
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrawlJob extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CrawlJobType jobType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ShopType shopType;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private JobStatus status = JobStatus.PENDING;

    @Builder.Default private Integer totalCount = 0;

    @Builder.Default private Integer successCount = 0;

    @Builder.Default private Integer failCount = 0;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    @Column(length = 2000)
    private String errorMessage;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public void start() {
        this.status = JobStatus.RUNNING;
        this.startedAt = LocalDateTime.now();
    }

    public void complete() {
        this.status = JobStatus.SUCCESS;
        this.completedAt = LocalDateTime.now();
    }

    public void fail(String errorMessage) {
        this.status = JobStatus.FAILED;
        this.errorMessage = errorMessage;
        this.completedAt = LocalDateTime.now();
    }

    public void incrementSuccess() {
        this.successCount++;
    }

    public void incrementFail() {
        this.failCount++;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public static CrawlJob create(CrawlJobType jobType, ShopType shopType) {
        return CrawlJob.builder()
                .jobType(jobType)
                .shopType(shopType)
                .status(JobStatus.PENDING)
                .totalCount(0)
                .successCount(0)
                .failCount(0)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
