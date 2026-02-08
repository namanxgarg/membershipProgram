package com.firstclub.membership.domain;

import java.time.LocalDateTime;

public class UserMembership {
    private String id;
    private String userId;
    private MembershipPlan plan;
    private MembershipTier tier;
    private MembershipStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime nextBillingDate;
    private Boolean autoRenew;

    public UserMembership(String id, String userId, MembershipPlan plan, 
                         MembershipTier tier) {
        this.id = id;
        this.userId = userId;
        this.plan = plan;
        this.tier = tier;
        this.status = MembershipStatus.ACTIVE;
        this.startDate = LocalDateTime.now();
        this.endDate = calculateEndDate(startDate, plan.getDurationMonths());
        this.nextBillingDate = endDate;
        this.autoRenew = true;
    }

    private LocalDateTime calculateEndDate(LocalDateTime start, Integer months) {
        return start.plusMonths(months);
    }
    public Boolean isActive() {
        return status == MembershipStatus.ACTIVE && 
               LocalDateTime.now().isBefore(endDate);
    }

    public Boolean isExpired() {
        return status == MembershipStatus.EXPIRED || 
               LocalDateTime.now().isAfter(endDate);
    }

    public void renew() {
        if (!autoRenew) {
            throw new IllegalStateException("Auto-renew is disabled");
        }
        LocalDateTime now = LocalDateTime.now();
        this.startDate = now;
        this.endDate = calculateEndDate(now, plan.getDurationMonths());
        this.nextBillingDate = endDate;
        this.status = MembershipStatus.ACTIVE;
    }

    public void cancel() {
        this.status = MembershipStatus.CANCELLED;
        this.autoRenew = false;
    }

    public void upgradeTier(MembershipTier newTier) {
        if (!newTier.isHigherThan(this.tier)) {
            throw new IllegalArgumentException("New tier must be higher");
        }
        this.tier = newTier;
    }

    public void downgradeTier(MembershipTier newTier) {
        if (newTier.isHigherThan(this.tier)) {
            throw new IllegalArgumentException("New tier must be lower");
        }
        this.tier = newTier;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public MembershipPlan getPlan() { return plan; }
    public MembershipTier getTier() { return tier; }
    public MembershipStatus getStatus() { return status; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public LocalDateTime getNextBillingDate() { return nextBillingDate; }
    public Boolean getAutoRenew() { return autoRenew; }
    
    public void setStatus(MembershipStatus status) { this.status = status; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public void setNextBillingDate(LocalDateTime nextBillingDate) { this.nextBillingDate = nextBillingDate; }
    public void setAutoRenew(Boolean autoRenew) { this.autoRenew = autoRenew; }
}
