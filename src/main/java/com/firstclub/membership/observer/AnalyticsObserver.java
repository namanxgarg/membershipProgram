package com.firstclub.membership.observer;

import com.firstclub.membership.domain.TierChangeEvent;

public class AnalyticsObserver implements ITierChangeObserver {
    @Override
    public void onTierChanged(TierChangeEvent event) {
        System.out.println("📊 Analytics: User " + event.getUserId() + 
                         " changed from " + event.getOldTierName() + 
                         " to " + event.getNewTierName() + 
                         " (Reason: " + event.getReason() + ")");
    }
}
