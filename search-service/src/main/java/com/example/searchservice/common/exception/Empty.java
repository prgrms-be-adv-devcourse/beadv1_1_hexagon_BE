package com.example.searchservice.common.exception;

public final class Empty {
    private static final Empty INSTANCE = new Empty();

    private Empty() {}

    public static Empty getInstance() {
        return INSTANCE;
    }
}
