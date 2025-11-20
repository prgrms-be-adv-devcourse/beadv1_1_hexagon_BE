package com.example.communicationservice.common.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = EmptySerializer.class)
public final class Empty {
    private static final Empty INSTANCE = new Empty();

    private Empty() {}

    public static Empty getInstance() {
        return INSTANCE;
    }

}
