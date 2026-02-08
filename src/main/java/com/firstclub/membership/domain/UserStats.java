package com.firstclub.membership.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UserStats {
    private Integer orderCount;
    private BigDecimal totalOrderValue;
    private LocalDateTime lastUpdatedAt;

    public UserStats(Integer orderCount, BigDecimal totalOrderValue) {
        this.orderCount = orderCount;
        this.totalOrderValue = totalOrderValue;
        this.lastUpdatedAt = LocalDateTime.now();
    }

    // Getters
    public Integer getOrderCount() { return orderCount; }
    public BigDecimal getTotalOrderValue() { return totalOrderValue; }
    public LocalDateTime getLastUpdatedAt() { return lastUpdatedAt; }
}
