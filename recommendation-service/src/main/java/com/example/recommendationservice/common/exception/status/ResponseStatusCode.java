package com.example.recommendationservice.common.exception.status;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ResponseStatusCode {

    // 요청 성공
    SUCCESS(0, "요청이 성공하였습니다.", OK);

    private final int code;
    private final String message;
    private final int httpStatusCode;

    ResponseStatusCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatus.value();
    }

}
