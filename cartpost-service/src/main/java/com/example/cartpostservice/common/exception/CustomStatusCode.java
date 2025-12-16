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

    FORBIDDEN_ITEM(HttpStatus.FORBIDDEN, 6403, "소유하고 있는 아이템이 아닙니다"),
    FORBIDDEN_COMMISSION(HttpStatus.FORBIDDEN, 6413, "의뢰글에 접근 권한이 없습니다"),
    NOT_OPEN_COMMISSION(HttpStatus.UNAUTHORIZED, 6423, "중단된 의뢰글은 다시 모집 공고를 열 수 없습니다"),

    BAD_REQUEST_ITEM(HttpStatus.BAD_REQUEST, 6404, "아이템이 존재하지 않습니다"),
    BAD_REQUEST_COMMISSION(HttpStatus.BAD_REQUEST, 6414, "의뢰글이 존재하지 않습니다"),
    BAD_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, 6424, "파리미터 값이 잘못되었습니다"),
    ALREADY_CLOSED_COMMISSION(HttpStatus.BAD_REQUEST, 6434, "이미 마감된 의뢰글 입니다"),
    BAD_REQUEST_INTERNAL_MODULE_SERVER(HttpStatus.BAD_REQUEST, 6444, "내부 모듈 시스템에 잘못된 요청을 보냈습니다"),

    NOT_FOUND_INTERNAL_MODULE_SERVER(HttpStatus.NOT_FOUND, 6454, "내부 모듈 시스템에서 값을 찾지 못하였습니다"),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 6500, "내부 시스템에 오류가 발생했습니다"),
    NOT_FOUND_CART(HttpStatus.INTERNAL_SERVER_ERROR, 6510, "내부 서버 문제로 장바구니가 없습니다"),
    NOT_FOUND_MEMBER_INFO(HttpStatus.INTERNAL_SERVER_ERROR, 6520, "내부 서버 문제로 사용자 정보가 없습니다"),
    INTERNAL_MODULE_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 6530, "내부 모듈 시스템과 통신 중 오류가 발생했습니다"),

    SERVICE_MODULE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, 6503, "내부 모듈 시스템에서 요청 처리할 준비가 되어 있지 않습니다");


    private final HttpStatus status;
    private final int code;
    private final String message;
}
