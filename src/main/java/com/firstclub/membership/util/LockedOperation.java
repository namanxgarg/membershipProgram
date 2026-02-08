package com.firstclub.membership.util;

@FunctionalInterface
public interface LockedOperation<T> {
    T execute() throws Exception;
}
