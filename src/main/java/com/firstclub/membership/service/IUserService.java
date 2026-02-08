package com.firstclub.membership.service;

import com.firstclub.membership.domain.UserStats;
import java.util.List;

public interface IUserService {
    UserStats getOrderStats(String userId, Integer timeWindowDays);
    List<String> getUserCohorts(String userId);
    Integer getOrderCountInWindow(String userId, Integer days);
    java.math.BigDecimal getOrderValueInWindow(String userId, Integer days);
}
