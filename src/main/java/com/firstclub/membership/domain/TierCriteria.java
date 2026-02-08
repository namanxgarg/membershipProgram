package com.firstclub.membership.domain;

import java.math.BigDecimal;

public class TierCriteria {
    private String id;
    private String tierId;
    private CriteriaType type;
    private BigDecimal threshold;
    private Integer timeWindowDays;
    private String cohortId;
    private LogicalOperator operator;

    public TierCriteria(String id, String tierId, CriteriaType type) {
        this.id = id;
        this.tierId = tierId;
        this.type = type;
        this.operator = LogicalOperator.OR;
    }

    public TierCriteria withThreshold(BigDecimal threshold) {
        this.threshold = threshold;
        return this;
    }

    public TierCriteria withTimeWindow(Integer days) {
        this.timeWindowDays = days;
        return this;
    }

    public TierCriteria withCohortId(String cohortId) {
        this.cohortId = cohortId;
        return this;
    }

    public TierCriteria withOperator(LogicalOperator operator) {
        this.operator = operator;
        return this;
    }
    public CriteriaType getType() { return type; }
    public BigDecimal getThreshold() { return threshold; }
    public Integer getTimeWindowDays() { return timeWindowDays; }
    public String getCohortId() { return cohortId; }
    public LogicalOperator getOperator() { return operator; }
}
