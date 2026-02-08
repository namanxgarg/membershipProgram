package com.firstclub.membership.calculator;

import com.firstclub.membership.domain.CriteriaType;
import com.firstclub.membership.service.IUserService;
import java.util.HashMap;
import java.util.Map;

public class CriteriaStrategyFactory {
    private final Map<CriteriaType, ICriteriaStrategy> strategies;
    
    public CriteriaStrategyFactory(IUserService userService) {
        this.strategies = new HashMap<>();
        this.strategies.put(CriteriaType.ORDER_COUNT, 
            new OrderCountCriteriaStrategy(userService));
        this.strategies.put(CriteriaType.ORDER_VALUE, 
            new OrderValueCriteriaStrategy(userService));
        this.strategies.put(CriteriaType.COHORT, 
            new CohortCriteriaStrategy(userService));
    }
    
    /**
     * Get the appropriate strategy for the given criteria type
     * 
     * @param criteriaType The type of criteria
     * @return The strategy implementation, or null if type not supported
     */
    public ICriteriaStrategy getStrategy(CriteriaType criteriaType) {
        return strategies.get(criteriaType);
    }
}
