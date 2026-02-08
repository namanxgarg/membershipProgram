package com.firstclub.membership.observer;

import com.firstclub.membership.domain.TierChangeEvent;
import java.util.ArrayList;
import java.util.List;

public class TierChangeNotifier {
    private final List<ITierChangeObserver> observers = new ArrayList<>();
    
    /**
     * Register an observer to be notified of tier changes
     * 
     * @param observer The observer to register
     */
    public void addObserver(ITierChangeObserver observer) {
        observers.add(observer);
    }
    
    /**
     * Remove an observer
     * 
     * @param observer The observer to remove
     */
    public void removeObserver(ITierChangeObserver observer) {
        observers.remove(observer);
    }
    
    /**
     * Notify all observers of a tier change
     * 
     * @param event The tier change event
     */
    public void notifyObservers(TierChangeEvent event) {
        for (ITierChangeObserver observer : observers) {
            try {
                observer.onTierChanged(event);
            } catch (Exception e) {
                // Don't let one observer's failure stop others
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }
    
    /**
     * Get number of registered observers
     */
    public int getObserverCount() {
        return observers.size();
    }
}
