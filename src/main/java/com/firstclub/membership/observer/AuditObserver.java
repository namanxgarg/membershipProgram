package com.firstclub.membership.observer;

import com.firstclub.membership.domain.TierChangeEvent;

public class AuditObserver implements ITierChangeObserver {
    @Override
    public void onTierChanged(TierChangeEvent event) {
        System.out.println("📝 Audit Log: Tier change for user " + event.getUserId() + 
                         " at " + event.getChangedAt() + 
                         " - " + event.getOldTierName() + " → " + 
                         event.getNewTierName());
    }
}
