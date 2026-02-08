package com.firstclub.membership.util;

public interface IDistributedLock {
    LockHandle acquireLock(String key, Integer timeoutSeconds);
    void releaseLock(LockHandle handle);
    <T> T executeWithLock(String key, Integer timeoutSeconds, 
                         LockedOperation<T> operation);
}
