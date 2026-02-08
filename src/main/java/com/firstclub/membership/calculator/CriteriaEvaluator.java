package com.firstclub.membership.calculator;

import com.firstclub.membership.domain.*;
import com.firstclub.membership.service.IUserService;

public class CriteriaEvaluator {
    private CriteriaStrategyFactory strategyFactory;
    private IUserService userService;
    
    public CriteriaEvaluator(IUserService userService) {
        this.userService = userService;
        this.strategyFactory = new CriteriaStrategyFactory(userService);
    }
    
    public Boolean evaluate(TierCriteria criteria, String userId) {
        ICriteriaStrategy strategy = strategyFactory.getStrategy(criteria.getType());
        
        if (strategy == null) {
            return false;
        }
        
        return strategy.evaluate(criteria, userId);
    }
    
    public UserStats getStatsInWindow(String userId, Integer days) {
        return userService.getOrderStats(userId, days);
    }
}
