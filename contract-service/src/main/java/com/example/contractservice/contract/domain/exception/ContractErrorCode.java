package com.example.contractservice.contract.domain.exception;

import org.springframework.http.HttpStatus;

public enum ContractErrorCode {
    NOT_FREELANCER(HttpStatus.BAD_REQUEST, 4000, "프리랜서가 아닙니다. 역할을 확인해 주세요."),
    INVALID_MEMBER(HttpStatus.BAD_REQUEST, 4001, "적절하지 않은 회원입니다."),
    DELETED_MEMBER(HttpStatus.BAD_REQUEST, 4002, "탈퇴한 회원은 계약을 생성할 수 없습니다."),

    NO_CONTRACT(HttpStatus.BAD_REQUEST, 4010, "해당 계약이 존재하지 않습니다."), // TODO: 계약 코드를 넣을 수 있도록 개선
    NOT_REQUESTED_STATUS(HttpStatus.BAD_REQUEST, 4012, "요청 상태인 계약만 성립할 수 있습니다."),

    INVALID_PAYMENT_MEMBER(HttpStatus.BAD_REQUEST, 4020, "현재 로그인한 회원만이 자신의 계약을 결제할 수 있으며 클라이언트여야 합니다."),
    NOT_CONFIRMED_STATUS(HttpStatus.BAD_REQUEST, 4021, "계약 성사 상태인 계약만 결제할 수 있습니다."),

    INVALID_MEMBER_COUNT(HttpStatus.BAD_REQUEST, 4030, "계약 참여자 수를 만족하지 않습니다.");

    private final int httpStatusCode;
    private final int statusCode;
    private final String message;

    ContractErrorCode(HttpStatus httpStatusCode, int statusCode, String message) {
        this.httpStatusCode = httpStatusCode.value();
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
