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

import java.time.LocalDate;

@Entity
@Table(name = "holidays", indexes = {@Index(name = "idx_holiday_date", columnList = "holidayDate")})
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Holiday extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private LocalDate holidayDate;

    private LocalDate monitoringStartDate;

    private LocalDate monitoringEndDate;

    private Integer year;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    public void updateMonitoringPeriod(LocalDate startDate, LocalDate endDate) {
        this.monitoringStartDate = startDate;
        this.monitoringEndDate = endDate;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }

    public boolean isInMonitoringPeriod(LocalDate date) {
        if (monitoringStartDate == null || monitoringEndDate == null) {
            return false;
        }
        return !date.isBefore(monitoringStartDate) && !date.isAfter(monitoringEndDate);
    }

    public static Holiday createWithDefaultMonitoring(String name, LocalDate holidayDate) {
        return Holiday.builder()
                .name(name)
                .holidayDate(holidayDate)
                .monitoringStartDate(holidayDate.minusDays(14))
                .monitoringEndDate(holidayDate.plusDays(7))
                .year(holidayDate.getYear())
                .isActive(true)
                .build();
    }
}
