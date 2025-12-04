package com.example.memberservice.common.redis.model.enums;

public enum RedisKeyPrefix {
    EMAIL("EMAIL:"),
    TOKEN("TOKEN:");

    private final String prefix;

    RedisKeyPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String build(String key) {
        return prefix + key;
    }
}
