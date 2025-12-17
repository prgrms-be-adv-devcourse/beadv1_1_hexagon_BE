package com.example.recommendationservice.common.exception.status;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ResponseStatusCode {

    // 요청 성공
    SUCCESS(0, "요청이 성공하였습니다.", OK),

    // 프리랜서 추천 관련 예외
    INVALID_RECOMMENDATION_COUNT(60000, "추천받을 프리랜서 수는 1 이상이어야 합니다.", BAD_REQUEST);

    private final int code;
    private final String message;
    private final int httpStatusCode;

    ResponseStatusCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatus.value();
    }

}
