package com.firstclub.membership.domain;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;

public class MembershipTier {
    private String id;
    private String name;
    private Integer level;
    private Boolean isActive;
    private List<TierCriteria> criteria;
    private List<TierBenefit> benefits;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MembershipTier(String id, String name, Integer level) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.isActive = true;
        this.criteria = new ArrayList<>();
        this.benefits = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Integer getLevel() { return level; }
    public Boolean getIsActive() { return isActive; }
    public List<TierCriteria> getCriteria() { return criteria; }
    public List<TierBenefit> getBenefits() { return benefits; }

    public void addCriteria(TierCriteria criterion) {
        this.criteria.add(criterion);
    }

    public void addBenefit(TierBenefit benefit) {
        this.benefits.add(benefit);
    }

    public Boolean isHigherThan(MembershipTier other) {
        return this.level > other.level;
    }
}
