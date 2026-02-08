package com.firstclub.membership.util;

public class LockHandle {
    private String key;
    private String lockId;
    
    public LockHandle(String key, String lockId) {
        this.key = key;
        this.lockId = lockId;
    }
    
    public String getKey() { return key; }
    public String getLockId() { return lockId; }
}
