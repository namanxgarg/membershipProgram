package com.firstclub.membership.calculator;

import com.firstclub.membership.domain.TierCriteria;
import com.firstclub.membership.service.IUserService;

public class OrderCountCriteriaStrategy implements ICriteriaStrategy {
    private IUserService userService;
    
    public OrderCountCriteriaStrategy(IUserService userService) {
        this.userService = userService;
    }
    
    @Override
    public Boolean evaluate(TierCriteria criteria, String userId) {
        Integer timeWindow = criteria.getTimeWindowDays();
        Integer orderCount = userService.getOrderCountInWindow(userId, timeWindow);
        Integer threshold = criteria.getThreshold().intValue();
        return orderCount >= threshold;
    }
}
