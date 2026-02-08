package com.firstclub.membership.manager;

import com.firstclub.membership.domain.*;
import com.firstclub.membership.repository.*;
import com.firstclub.membership.calculator.CriteriaEvaluator;
import com.firstclub.membership.observer.TierChangeNotifier;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class TierManager {
    private ITierRepository tierRepository;
    private IMembershipRepository membershipRepository;
    private CriteriaEvaluator criteriaEvaluator;
    private TierChangeNotifier tierChangeNotifier;
    private static final Map<String, Object> locks = new ConcurrentHashMap<>();
    
    public TierManager(ITierRepository tierRepository,
                     IMembershipRepository membershipRepository,
                     CriteriaEvaluator criteriaEvaluator,
                     TierChangeNotifier tierChangeNotifier) {
        this.tierRepository = tierRepository;
        this.membershipRepository = membershipRepository;
        this.criteriaEvaluator = criteriaEvaluator;
        this.tierChangeNotifier = tierChangeNotifier;
    }
    
    public MembershipTier recalculateTierForUser(String userId) {
        Object lock = locks.computeIfAbsent(userId, k -> new Object());
        synchronized (lock) {
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
        }
    }
    
    public void upgradeTier(String userId, String newTierId, 
                           TierChangeReason reason) {
        changeTier(userId, newTierId, reason, true);
    }
    
    public void downgradeTier(String userId, String newTierId, 
                              TierChangeReason reason) {
        changeTier(userId, newTierId, reason, false);
    }
    
    private void changeTier(String userId, String newTierId, 
                           TierChangeReason reason, boolean isUpgrade) {
        UserMembership membership = membershipRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Membership not found"));
        
        MembershipTier newTier = tierRepository.findById(newTierId)
            .orElseThrow(() -> new RuntimeException("Tier not found"));
        
        MembershipTier oldTier = membership.getTier();
        
        if (isUpgrade) {
            membership.upgradeTier(newTier);
        } else {
            membership.downgradeTier(newTier);
        }
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
}
