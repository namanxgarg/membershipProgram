package com.firstclub.membership.domain;

import java.math.BigDecimal;

public class TierBenefit {
    private String id;
    private String tierId;
    private BenefitType type;
    private BigDecimal value;
    private String categoryId;
    private Boolean isActive;

    public TierBenefit(String id, String tierId, BenefitType type) {
        this.id = id;
        this.tierId = tierId;
        this.type = type;
        this.isActive = true;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }

    public TierBenefit withValue(BigDecimal value) {
        this.value = value;
        return this;
    }

    public TierBenefit withCategoryId(String categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public Boolean appliesToCategory(String categoryId) {
        if (this.categoryId == null) {
            return true;
        }
        return this.categoryId.equals(categoryId);
    }

    public BigDecimal calculateDiscount(BigDecimal amount) {
        if (type != BenefitType.DISCOUNT_PERCENTAGE || value == null) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(value).divide(new BigDecimal("100"));
    }
    public BenefitType getType() { return type; }
    public BigDecimal getValue() { return value; }
    public String getCategoryId() { return categoryId; }
}
