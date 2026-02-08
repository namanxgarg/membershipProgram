package com.firstclub.membership.service;

import com.firstclub.membership.domain.*;
import com.firstclub.membership.repository.*;
import com.firstclub.membership.manager.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.math.BigDecimal;

public class MembershipService implements IMembershipBenefitProvider {
    private IPlanRepository planRepository;
    private ITierRepository tierRepository;
    private IMembershipRepository membershipRepository;
    private TierManager tierManager;
    private RenewalManager renewalManager;
    private IUserService userService;
    private IPaymentService paymentService;
    
    public MembershipService(IPlanRepository planRepository,
                           ITierRepository tierRepository,
                           IMembershipRepository membershipRepository,
                           TierManager tierManager,
                           RenewalManager renewalManager,
                           IUserService userService,
                           IPaymentService paymentService) {
        this.planRepository = planRepository;
        this.tierRepository = tierRepository;
        this.membershipRepository = membershipRepository;
        this.tierManager = tierManager;
        this.renewalManager = renewalManager;
        this.userService = userService;
        this.paymentService = paymentService;
    }
    
    public List<MembershipPlan> getAvailablePlans() {
        return planRepository.findAllActive();
    }
    
    public List<MembershipTier> getAvailableTiers() {
        return tierRepository.findAllActive();
    }
    
    public UserMembership subscribe(String userId, String planId, String tierId) {
        MembershipPlan plan = planRepository.findById(planId)
            .orElseThrow(() -> new IllegalArgumentException("Plan not found"));
        plan.validate();
        
        MembershipTier tier = tierRepository.findById(tierId)
            .orElseThrow(() -> new IllegalArgumentException("Tier not found"));
        
        Optional<UserMembership> existing = membershipRepository.findByUserId(userId);
        if (existing.isPresent() && existing.get().isActive()) {
            throw new IllegalStateException("User already has active membership");
        }
        
        PaymentResult paymentResult = paymentService.processPayment(
            userId,
            plan.getPrice(),
            "Membership subscription for " + plan.getName()
        );
        
        if (!paymentResult.isSuccess()) {
            throw new RuntimeException("Payment failed: " + paymentResult.getErrorMessage());
        }
        
        String membershipId = generateId();
        UserMembership membership = new UserMembership(
            membershipId, userId, plan, tier
        );
        
        return membershipRepository.save(membership);
    }
    
    public void cancelMembership(String userId) {
        UserMembership membership = membershipRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Membership not found"));
        
        membership.cancel();
        membershipRepository.update(membership);
    }
    
    public UserMembership upgradePlan(String userId, String newPlanId) {
        UserMembership membership = membershipRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Membership not found"));
        
        MembershipPlan newPlan = planRepository.findById(newPlanId)
            .orElseThrow(() -> new IllegalArgumentException("Plan not found"));
        
        membership = new UserMembership(
            membership.getId(), userId, newPlan, membership.getTier()
        );
        
        return membershipRepository.update(membership);
    }
    
    public Optional<UserMembership> getCurrentMembership(String userId) {
        return membershipRepository.findByUserId(userId);
    }
    
    public void onOrderCompleted(String userId, BigDecimal orderValue) {
        tierManager.recalculateTierForUser(userId);
    }
    
    @Override
    public MembershipBenefits getMembershipBenefits(String userId) {
        UserMembership membership = membershipRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Membership not found"));
        
        if (!membership.isActive()) {
            return new MembershipBenefits("", "");
        }
        
        MembershipTier tier = membership.getTier();
        MembershipBenefits benefits = new MembershipBenefits(
            tier.getId(), tier.getName()
        );
        
        for (TierBenefit benefit : tier.getBenefits()) {
            if (benefit.getIsActive()) {
                benefits.addBenefit(benefit);
            }
        }
        
        return benefits;
    }
    
    @Override
    public List<TierBenefit> getTierBenefits(String tierId) {
        MembershipTier tier = tierRepository.findById(tierId)
            .orElseThrow(() -> new RuntimeException("Tier not found"));
        return tier.getBenefits();
    }
    
    @Override
    public Boolean isFreeDeliveryEligible(String userId) {
        MembershipBenefits benefits = getMembershipBenefits(userId);
        return benefits.getFreeDelivery() != null && benefits.getFreeDelivery();
    }
    
    @Override
    public BigDecimal getDiscountPercentage(String userId, String categoryId) {
        MembershipBenefits benefits = getMembershipBenefits(userId);
        Map<String, BigDecimal> discounts = benefits.getDiscountPercentage();
        
        if (categoryId != null && discounts.containsKey(categoryId)) {
            return discounts.get(categoryId);
        }
        
        return discounts.getOrDefault("all", BigDecimal.ZERO);
    }
    
    private String generateId() {
        return "m_" + System.currentTimeMillis() + "_" + 
               (int)(Math.random() * 1000);
    }
}
