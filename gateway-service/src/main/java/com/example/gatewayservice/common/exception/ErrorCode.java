package com.example.gatewayservice.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    //2500
    UNAUTHORIZATION(401, 2500,"인증되지 않은 요청입니다."),
    NEED_RE_LOGIN(401, 2501, "로그인을 다시 해주세요."),
    NEED_RE_ISSUE(401, 2502, "AccessToken을 재발행해주세요."),
    NEED_SIGNUP(403, 2503, "회원가입이 필요합니다."),
    FORBIDDEN(403, 2504, "요청을 수행 할 권한이 부족합니다.");


    private final int statusCode;
    private final int code;
    private final String message;

    ErrorCode(int statusCode, int code, String message) {
        this.message = message;
        this.code = code;
        this.statusCode = statusCode;
    }

}
