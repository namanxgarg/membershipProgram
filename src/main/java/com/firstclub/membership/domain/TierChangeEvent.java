package com.firstclub.membership.domain;

import java.time.LocalDateTime;

public class TierChangeEvent {
    private String userId;
    private String oldTierId;
    private String oldTierName;
    private String newTierId;
    private String newTierName;
    private TierChangeReason reason;
    private LocalDateTime changedAt;
    
    public TierChangeEvent(String userId, String oldTierId, String oldTierName,
                          String newTierId, String newTierName, TierChangeReason reason) {
        this.userId = userId;
        this.oldTierId = oldTierId;
        this.oldTierName = oldTierName;
        this.newTierId = newTierId;
        this.newTierName = newTierName;
        this.reason = reason;
        this.changedAt = LocalDateTime.now();
    }
    
    public String getUserId() { return userId; }
    public String getOldTierId() { return oldTierId; }
    public String getOldTierName() { return oldTierName; }
    public String getNewTierId() { return newTierId; }
    public String getNewTierName() { return newTierName; }
    public TierChangeReason getReason() { return reason; }
    public LocalDateTime getChangedAt() { return changedAt; }
    
    public Boolean isUpgrade() {
        return reason == TierChangeReason.AUTO_UPGRADE || 
               reason == TierChangeReason.MANUAL_UPGRADE;
    }
    
    public Boolean isDowngrade() {
        return reason == TierChangeReason.AUTO_DOWNGRADE || 
               reason == TierChangeReason.MANUAL_DOWNGRADE;
    }
}
