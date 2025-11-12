package com.example.searchservice.common.response;

public record BaseResponse<T>(
        int statusCode,
        String message,
        T data
) {

}
