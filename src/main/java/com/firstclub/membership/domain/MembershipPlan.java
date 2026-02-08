package com.firstclub.membership.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MembershipPlan {
    private String id;
    private String name;
    private BigDecimal price;
    private Integer durationMonths;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MembershipPlan(String id, String name, BigDecimal price, 
                          Integer durationMonths) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.durationMonths = durationMonths;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getDurationMonths() {
        return durationMonths;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
        this.updatedAt = LocalDateTime.now();
    }

    public void validate() {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Plan ID cannot be empty");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        if (durationMonths == null || durationMonths <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
    }
}
