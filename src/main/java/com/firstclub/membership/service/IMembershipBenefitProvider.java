package com.firstclub.membership.service;

import com.firstclub.membership.domain.MembershipBenefits;
import com.firstclub.membership.domain.TierBenefit;
import java.util.List;
import java.math.BigDecimal;

// Interface for checkout service
public interface IMembershipBenefitProvider {
    MembershipBenefits getMembershipBenefits(String userId);
    List<TierBenefit> getTierBenefits(String tierId);
    Boolean isFreeDeliveryEligible(String userId);
    BigDecimal getDiscountPercentage(String userId, String categoryId);
}
