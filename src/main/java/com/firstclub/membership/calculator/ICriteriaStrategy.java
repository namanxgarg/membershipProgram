package com.firstclub.membership.calculator;

import com.firstclub.membership.domain.TierCriteria;

public interface ICriteriaStrategy {
    /**
     * Evaluate if the criteria is met for the given user
     * 
     * @param criteria The criteria to evaluate
     * @param userId The user ID to check
     * @return true if criteria is met, false otherwise
     */
    Boolean evaluate(TierCriteria criteria, String userId);
}
