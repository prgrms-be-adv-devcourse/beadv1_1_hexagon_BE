package com.example.searchservice.common.exception;

public interface ErrorCode {
    int getCode();
    int getHttpStatus();
    String getMessage();
}
