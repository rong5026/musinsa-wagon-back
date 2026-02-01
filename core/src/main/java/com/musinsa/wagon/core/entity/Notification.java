package com.musinsa.wagon.core.entity;

import com.musinsa.wagon.core.entity.enums.NotificationType;

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
        name = "notifications",
        indexes = {@Index(name = "idx_notification_user_read", columnList = "user_id, isRead")})
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType notificationType;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 1000)
    private String message;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isRead = false;

    private LocalDateTime sentAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public void markAsRead() {
        this.isRead = true;
    }

    public void markAsSent() {
        this.sentAt = LocalDateTime.now();
    }

    public static Notification create(
            User user,
            Product product,
            NotificationType type,
            String title,
            String message) {
        return Notification.builder()
                .user(user)
                .product(product)
                .notificationType(type)
                .title(title)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
