package com.firstclub.membership.observer;

import com.firstclub.membership.domain.TierChangeEvent;

public class NotificationObserver implements ITierChangeObserver {
    @Override
    public void onTierChanged(TierChangeEvent event) {
        if (event.isUpgrade()) {
            System.out.println("📧 Sending email to user " + event.getUserId() + 
                             ": Congratulations! You've been upgraded to " + 
                             event.getNewTierName() + " tier!");
        } else if (event.isDowngrade()) {
            System.out.println("📧 Sending email to user " + event.getUserId() + 
                             ": Your tier has been updated to " + 
                             event.getNewTierName() + " tier.");
        }
    }
}
