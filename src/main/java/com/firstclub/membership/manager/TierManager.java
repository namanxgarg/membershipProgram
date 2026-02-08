package com.firstclub.membership.manager;

import com.firstclub.membership.domain.*;
import com.firstclub.membership.repository.*;
import com.firstclub.membership.calculator.CriteriaEvaluator;
import com.firstclub.membership.observer.TierChangeNotifier;
import com.firstclub.membership.util.IDistributedLock;
import java.util.List;
import java.math.BigDecimal;

public class TierManager {
    private ITierRepository tierRepository;
    private IMembershipRepository membershipRepository;
    private CriteriaEvaluator criteriaEvaluator;
    private IDistributedLock distributedLock;
    private TierChangeNotifier tierChangeNotifier;
    
    public TierManager(ITierRepository tierRepository,
                     IMembershipRepository membershipRepository,
                     CriteriaEvaluator criteriaEvaluator,
                     IDistributedLock distributedLock,
                     TierChangeNotifier tierChangeNotifier) {
        this.tierRepository = tierRepository;
        this.membershipRepository = membershipRepository;
        this.criteriaEvaluator = criteriaEvaluator;
        this.distributedLock = distributedLock;
        this.tierChangeNotifier = tierChangeNotifier;
    }
    
    public MembershipTier recalculateTierForUser(String userId) {
        String lockKey = "tier_recalc_" + userId;
        
        return distributedLock.executeWithLock(lockKey, 30, () -> {
            UserMembership membership = membershipRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Membership not found"));
            
            MembershipTier newTier = calculateTier(userId);
            MembershipTier currentTier = membership.getTier();
            
            if (!newTier.getId().equals(currentTier.getId())) {
                if (newTier.getLevel() > currentTier.getLevel()) {
                    upgradeTier(userId, newTier.getId(), 
                               TierChangeReason.AUTO_UPGRADE);
                } else {
                    downgradeTier(userId, newTier.getId(), 
                                 TierChangeReason.AUTO_DOWNGRADE);
                }
            }
            
            return newTier;
        });
    }
    
    public void upgradeTier(String userId, String newTierId, 
                           TierChangeReason reason) {
        UserMembership membership = membershipRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Membership not found"));
        
        MembershipTier newTier = tierRepository.findById(newTierId)
            .orElseThrow(() -> new RuntimeException("Tier not found"));
        
        MembershipTier oldTier = membership.getTier();
        
        membership.upgradeTier(newTier);
        membershipRepository.update(membership);
        
        TierChangeEvent event = new TierChangeEvent(
            userId,
            oldTier.getId(),
            oldTier.getName(),
            newTier.getId(),
            newTier.getName(),
            reason
        );
        tierChangeNotifier.notifyObservers(event);
    }
    
    public void downgradeTier(String userId, String newTierId, 
                              TierChangeReason reason) {
        UserMembership membership = membershipRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Membership not found"));
        
        MembershipTier newTier = tierRepository.findById(newTierId)
            .orElseThrow(() -> new RuntimeException("Tier not found"));
        
        MembershipTier oldTier = membership.getTier();
        
        membership.downgradeTier(newTier);
        membershipRepository.update(membership);
        
        TierChangeEvent event = new TierChangeEvent(
            userId,
            oldTier.getId(),
            oldTier.getName(),
            newTier.getId(),
            newTier.getName(),
            reason
        );
        tierChangeNotifier.notifyObservers(event);
    }
    
    public MembershipTier getEligibleTier(String userId) {
        return calculateTier(userId);
    }
    
    public MembershipTier calculateTier(String userId) {
        List<MembershipTier> tiers = tierRepository.findAllOrderedByLevel();
        
        for (MembershipTier tier : tiers) {
            if (evaluateTierCriteria(tier, userId)) {
                return tier;
            }
        }
        
        return tierRepository.findByLevel(1)
            .orElseThrow(() -> new RuntimeException("Default tier not found"));
    }
    
    private Boolean evaluateTierCriteria(MembershipTier tier, String userId) {
        List<TierCriteria> criteria = tier.getCriteria();
        
        if (criteria.isEmpty()) {
            return true;
        }
        
        Boolean allMatch = true;
        Boolean anyMatch = false;
        
        for (TierCriteria criterion : criteria) {
            Boolean matches = criteriaEvaluator.evaluate(criterion, userId);
            
            if (criterion.getOperator() == LogicalOperator.AND) {
                allMatch = allMatch && matches;
            } else {
                anyMatch = anyMatch || matches;
            }
        }
        
        return anyMatch || allMatch;
    }
    
    public MembershipTier findHighestEligibleTier(String userId) {
        return calculateTier(userId);
    }
}
