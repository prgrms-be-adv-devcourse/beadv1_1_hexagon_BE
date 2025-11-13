package com.example.searchservice.common.vo;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SearchScope {
    ALL,
    TITLE,
    CONTENT;

    @JsonCreator
    public static SearchScope from(String value) {
        if (value == null) return ALL;
        return SearchScope.valueOf(value.toUpperCase());
    }
}
