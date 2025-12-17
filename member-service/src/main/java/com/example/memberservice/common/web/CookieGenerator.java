package com.example.memberservice.common.web;

import org.springframework.http.ResponseCookie;

public class CookieGenerator {

    public static String createCookies(String key, String value, long age) {
        ResponseCookie cookie = ResponseCookie.from(key, value)
            .path("/")
            .maxAge(age)
            .secure(true)
            .httpOnly(true)
            .sameSite("None")
            .build();

        return cookie.toString();
    }
}