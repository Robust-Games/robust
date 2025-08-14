package com.robustgames.robustclient.business.factories;

public class IDFactory {
    private static long nextId = 1L;

    // Synchronisiert über mehrere Threads
    public static synchronized long generateId() {
        return nextId++;
    }
}
