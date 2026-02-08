package com.firstclub.membership.service;

import com.firstclub.membership.domain.UserStats;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockUserService implements IUserService {
    private Map<String, UserStats> statsMap = new HashMap<>();
    private Map<String, List<String>> cohortsMap = new HashMap<>();

    @Override
    public UserStats getOrderStats(String userId, Integer timeWindowDays) {
        UserStats stats = statsMap.get(userId);
        if (stats == null) {
            stats = new UserStats(0, BigDecimal.ZERO);
        }
        return stats;
    }

    @Override
    public List<String> getUserCohorts(String userId) {
        return cohortsMap.getOrDefault(userId, new ArrayList<>());
    }

    @Override
    public Integer getOrderCountInWindow(String userId, Integer days) {
        return getOrderStats(userId, days).getOrderCount();
    }

    @Override
    public java.math.BigDecimal getOrderValueInWindow(String userId, Integer days) {
        return getOrderStats(userId, days).getTotalOrderValue();
    }

    public void setUserStats(String userId, UserStats stats) {
        statsMap.put(userId, stats);
    }

    public void addUserToCohort(String userId, String cohortId) {
        cohortsMap.computeIfAbsent(userId, k -> new ArrayList<>())
                  .add(cohortId);
    }
    
    public void incrementOrderCount(String userId, Integer count) {
        UserStats current = statsMap.getOrDefault(userId, 
            new UserStats(0, BigDecimal.ZERO));
        statsMap.put(userId, new UserStats(
            current.getOrderCount() + count,
            current.getTotalOrderValue()
        ));
    }
    
    public void addOrderValue(String userId, BigDecimal value) {
        UserStats current = statsMap.getOrDefault(userId, 
            new UserStats(0, BigDecimal.ZERO));
        statsMap.put(userId, new UserStats(
            current.getOrderCount(),
            current.getTotalOrderValue().add(value)
        ));
    }
}
