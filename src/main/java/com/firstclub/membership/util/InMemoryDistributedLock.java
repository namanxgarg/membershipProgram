package com.firstclub.membership.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class InMemoryDistributedLock implements IDistributedLock {
    private final Map<String, Object> locks = new ConcurrentHashMap<>();
    
    @Override
    public LockHandle acquireLock(String key, Integer timeoutSeconds) {
        Object lock = locks.computeIfAbsent(key, k -> new Object());
        return new LockHandle(key, "lock_" + System.currentTimeMillis());
    }
    
    @Override
    public void releaseLock(LockHandle handle) {
    }
    
    @Override
    public <T> T executeWithLock(String key, Integer timeoutSeconds, 
                                 LockedOperation<T> operation) {
        Object lock = locks.computeIfAbsent(key, k -> new Object());
        synchronized (lock) {
            try {
                return operation.execute();
            } catch (Exception e) {
                throw new RuntimeException("Error in locked operation", e);
            }
        }
    }
}
