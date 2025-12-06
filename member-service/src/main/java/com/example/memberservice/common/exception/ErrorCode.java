package com.example.memberservice.common.exception;


import org.springframework.http.HttpStatus;

public enum ErrorCode {

    //400
    VALIDATION_FAILED(2000, HttpStatus.BAD_REQUEST, "유효성 검증 실패"),
    EMAIL_VERIFICATION_CODE_MISMATCH(2001, HttpStatus.BAD_REQUEST,"이메일 인증에 실패했습니다."),
    NOT_CONTAINS_MEMBER_CODE(2002, HttpStatus.BAD_REQUEST, "검색하고자 하는 MemberCode는 반드시 포함되어야합니다."),
    EMAIL_VERIFICATION_BAD_ROLE_REQUEST(2003, HttpStatus.BAD_REQUEST,"Email 인증은 Freelancer 혹은 Client에 대해서만 가능합니다."),

    //401
    FAIL_LOGIN(2200, HttpStatus.UNAUTHORIZED, "로그인에 실패하였습니다."),
    UNAUTHORIZATION(2201, HttpStatus.UNAUTHORIZED, "인증되지 않은 요청입니다."),

    //404
    MEMBER_NOT_FOUND(2400, HttpStatus.NOT_FOUND, "요청하신 사용자를 찾을 수 없습니다."),
    INTERNAL_ILLEGAL_MEMBER_CODE(2401, HttpStatus.NOT_FOUND, "존재하지 않는 멤버 코드가 포함되어 있습니다."),
    NO_HANDLER(2402, HttpStatus.NOT_FOUND, "요청하신 리소스를 찾을 수 없습니다."),
    EMAIL_VERIFICATION_NOT_FOUND(2403,HttpStatus.NOT_FOUND,"이메일 인증 요청이 되지 않았습니다. 이메일 요청을 진행해주세요."),
    //409 Conflict
    MEMBER_ALREADY_EXISTS(2450, HttpStatus.CONFLICT, "이미 회원가입을 진행한 멤버입니다."),
    NICKNAME_ALREADY_EXISTS(2451, HttpStatus.CONFLICT, "이미 회원가입을 진행한 멤버입니다."),

    //429Too Many Requests
    EMAIL_VERIFICATION_EXCEEDED(2480, HttpStatus.TOO_MANY_REQUESTS,"인증 요청 가능 횟수를 초과하였습니다. 인증 코드를 재요청해주세요."),

    //500
    INTERNAL_SERVER_ERROR(2500, HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다."),
    DATA_SAVE_FAILED(2501, HttpStatus.INTERNAL_SERVER_ERROR, "데이터 저장에 실패했습니다."),
    MAIL_SEND_FAILED(2502,HttpStatus.INTERNAL_SERVER_ERROR, "메일 전송에 실패했습니다.");


    private final int code; // 2000번대

    private final int httpStatusCode;

    private final String message;

    ErrorCode(int code, HttpStatus httpStatusCode, String message) {
        this.code = code;
        this.httpStatusCode = httpStatusCode.value();
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getMessage() {
        return message;
    }

}
