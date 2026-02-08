package com.firstclub.membership.calculator;

import com.firstclub.membership.domain.TierCriteria;
import com.firstclub.membership.service.IUserService;

public class OrderValueCriteriaStrategy implements ICriteriaStrategy {
    private IUserService userService;
    
    public OrderValueCriteriaStrategy(IUserService userService) {
        this.userService = userService;
    }
    
    @Override
    public Boolean evaluate(TierCriteria criteria, String userId) {
        Integer timeWindow = criteria.getTimeWindowDays();
        java.math.BigDecimal orderValue = userService.getOrderValueInWindow(userId, timeWindow);
        java.math.BigDecimal threshold = criteria.getThreshold();
        return orderValue.compareTo(threshold) >= 0;
    }
}
