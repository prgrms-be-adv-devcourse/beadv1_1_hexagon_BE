package com.example.gatewayservice.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    //2500
    UNAUTHORIZATION(401, 2500,"인증되지 않은 요청입니다."),
    NEED_RE_LOGIN(401, 2501, "로그인을 다시 해주세요."),
    NEED_RE_ISSUE(401, 2502, "AccessToken을 재발행해주세요."),
    NEED_SIGNUP(403, 2503, "회원가입이 필요합니다."),
    FORBIDDEN_CLIENT(403, 2504, "CLIENT 권한이 필요한 요청입니다. 권한 등록 이후 시도해주세요."),
    FORBIDDEN_FREELANCER(403, 2505, "FREELANCER 권한이 필요한 요청입니다. 권한 등록 이후 시도해주세요.");


    private final int statusCode;
    private final int code;
    private final String message;

    ErrorCode(int statusCode, int code, String message) {
        this.message = message;
        this.code = code;
        this.statusCode = statusCode;
    }

}
