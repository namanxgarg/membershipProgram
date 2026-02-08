package com.firstclub.membership.observer;

import com.firstclub.membership.domain.TierChangeEvent;

public interface ITierChangeObserver {
    /**
     * Called when a tier change occurs
     * 
     * @param event The tier change event containing all details
     */
    void onTierChanged(TierChangeEvent event);
}
