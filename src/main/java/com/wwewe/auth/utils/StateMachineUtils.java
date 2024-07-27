package com.wwewe.auth.utils;

import com.tsm4j.core.StateListener;

public class StateMachineUtils {
    public static <T extends Enum<T>> StateListener<T> debugLoggingListener() {
        return context -> System.out.printf("[STATE]: %s%n", context.getLatestState());
    }
}
