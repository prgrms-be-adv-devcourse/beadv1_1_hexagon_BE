package com.example.searchservice.common.response;

import org.springframework.http.HttpStatus;

public record BaseResponse<T>(
        int code,
        int httpStatus,
        String message,
        T data
) {
    public static <T> BaseResponse<T> ok(T data) {
        return new BaseResponse<>(0, HttpStatus.OK.value(), "요청이 성공하였습니다.", data);
    }
}
