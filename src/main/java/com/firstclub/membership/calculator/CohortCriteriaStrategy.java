package com.firstclub.membership.calculator;

import com.firstclub.membership.domain.TierCriteria;
import com.firstclub.membership.service.IUserService;

public class CohortCriteriaStrategy implements ICriteriaStrategy {
    private IUserService userService;
    
    public CohortCriteriaStrategy(IUserService userService) {
        this.userService = userService;
    }
    
    @Override
    public Boolean evaluate(TierCriteria criteria, String userId) {
        java.util.List<String> userCohorts = userService.getUserCohorts(userId);
        return userCohorts.contains(criteria.getCohortId());
    }
}
