package org.hexagon.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

public record ResponseDto<T>(
        @Schema(description = "front - server 간 상태코드", defaultValue = "0")
        int code,

        @Schema(description = "Http Status 코드", defaultValue = "200")
        int httpStatus,

        @Schema(description = "상태 메시지", defaultValue = DEFAULT_SUCCESS_MESSAGE)
        String message,

        @Schema(description = "응답 데이터")
        T data
) {

    private static final int DEFAULT_SUCCESS_CODE = 0;
    private static final int DEFAULT_FAIL_CODE = 20000;

    private static final String DEFAULT_SUCCESS_MESSAGE = "요청이 성공하였습니다.";
    private static final String DEFAULT_FAIL_MESSAGE = "알 수 없는 에러로 요청을 처리할 수 없습니다.";

    // 요청에 성공한 경우 (결과 값 없음)
    public static ResponseDto<Empty> success() {
        return new ResponseDto<>(
                DEFAULT_SUCCESS_CODE,
                HttpStatus.OK.value(),
                DEFAULT_SUCCESS_MESSAGE,
                Empty.getInstance()
        );
    }

    // 요청에 성공한 경우 (결과 값 있음)
    public static <T> ResponseDto<T> success(T data) {
        return new ResponseDto<>(
                DEFAULT_SUCCESS_CODE,
                HttpStatus.OK.value(),
                DEFAULT_SUCCESS_MESSAGE,
                data
        );
    }

    public static ResponseDto<Empty> fail() {
        return new ResponseDto<>(
                DEFAULT_FAIL_CODE,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                DEFAULT_FAIL_MESSAGE,
                Empty.getInstance()
        );
    }

}
