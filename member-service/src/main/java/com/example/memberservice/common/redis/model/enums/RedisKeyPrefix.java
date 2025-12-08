package com.example.memberservice.common.redis.model.enums;

public enum RedisKeyPrefix {
    EMAIL_VERIFICATION_CODE("EMAIL:VERIFICATION:CODE:"),
    EMAIL_VERIFIED("EMAIL:VERIFIED:"),
    EMAIL_VERIFIED_COUNT("EMAIL:VERIFIED:COUNT:"),
    TOKEN("TOKEN:");

    private final String prefix;

    RedisKeyPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String build(String key) {
        return prefix + key;
    }
}
