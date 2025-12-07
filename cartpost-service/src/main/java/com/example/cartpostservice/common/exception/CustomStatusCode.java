package com.example.cartpostservice.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CustomStatusCode {
    SUCCESS(HttpStatus.OK, 0, "성공"),
    CREATED(HttpStatus.CREATED, 6201, "저장되었습니다."),

    SUCCESS_NO_DATA(HttpStatus.NO_CONTENT, 6204, "데이터가 존재하지 않습니다"),

    FORBIDDEN_ITEM(HttpStatus.FORBIDDEN, 6403, "소유하고 있는 아이템이 아닙니다" ),
    FORBIDDEN_COMMISSION(HttpStatus.FORBIDDEN, 6413, "의뢰글에 접근 권한이 없습니다"),

    NOT_FOUND_ITEM(HttpStatus.BAD_REQUEST, 6404, "아이템이 존재하지 않습니다"),
    NOT_FOUND_COMMISSION(HttpStatus.BAD_REQUEST, 6414, "의뢰글이 존재하지 않습니다"),

    NOT_FOUND_MEMBER(HttpStatus.INTERNAL_SERVER_ERROR, 6500, "내부 서버 문제로 장바구니가 없습니다"),
    NOT_FOUND_MEMBER_INFO(HttpStatus.INTERNAL_SERVER_ERROR, 6510, "내부 서버 문제로 사용자 정보가 없습니다"),
    EXTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 6520, "외부 서버 통신 장애");


    private final HttpStatus status;
    private final int code;
    private final String message;
}
