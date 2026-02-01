package com.musinsa.wagon.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "users",
        indexes = {@Index(name = "idx_user_email", columnList = "email", unique = true)})
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseAuditEntity {

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 50)
    private String nickname;

    @Builder.Default
    @Column(nullable = false)
    private Boolean notificationEnabled = true;

    @Column(length = 512)
    private String fcmToken;

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void toggleNotification(Boolean enabled) {
        this.notificationEnabled = enabled;
    }
}
