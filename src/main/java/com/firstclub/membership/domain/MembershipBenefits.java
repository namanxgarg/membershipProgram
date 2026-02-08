package com.firstclub.membership.domain;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;

public class MembershipBenefits {
    private String tierId;
    private String tierName;
    private List<TierBenefit> benefits;
    private Boolean freeDelivery;
    private Map<String, BigDecimal> discountPercentage;

    public MembershipBenefits(String tierId, String tierName) {
        this.tierId = tierId;
        this.tierName = tierName;
        this.benefits = new ArrayList<>();
        this.discountPercentage = new HashMap<>();
    }

    public void addBenefit(TierBenefit benefit) {
        this.benefits.add(benefit);
        
        if (benefit.getType() == BenefitType.DISCOUNT_PERCENTAGE && 
            benefit.getValue() != null) {
            String key = benefit.getCategoryId() != null ? 
                        benefit.getCategoryId() : "all";
            discountPercentage.put(key, benefit.getValue());
        }
        
        if (benefit.getType() == BenefitType.FREE_DELIVERY) {
            this.freeDelivery = true;
        }
    }
    public String getTierId() { return tierId; }
    public String getTierName() { return tierName; }
    public List<TierBenefit> getBenefits() { return benefits; }
    public Boolean getFreeDelivery() { return freeDelivery; }
    public Map<String, BigDecimal> getDiscountPercentage() { 
        return discountPercentage; 
    }
}
