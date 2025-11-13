package com.example.searchservice.common.response;

public record BaseResponse<T>(
        int code,
        int httpStatus,
        String message,
        T data
) {

}
