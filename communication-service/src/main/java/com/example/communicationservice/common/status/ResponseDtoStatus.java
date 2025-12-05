package com.example.communicationservice.common.status;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseDtoStatus {

    // 요청 성공
    SUCCESS(0, "요청이 성공하였습니다.", HttpStatus.OK),

    // 채팅방 관련 실패
    CHATROOM_INVALID_MEMBER_COUNT(1000, "1:1 채팅은 2명의 참여자가 필요합니다.", HttpStatus.BAD_REQUEST),
    CHATROOM_NOT_INCLUDE_SELF(1001, "현재 로그인한 사용자는 채팅방에 포함되어야 합니다.", HttpStatus.BAD_REQUEST),
    CHATROOM_ALREADY_EXISTS(1002, "이미 채팅방이 존재합니다.", HttpStatus.BAD_REQUEST),
    CHATROOM_INVALID_MEMBER(1003, "유효하지 않은 회원은 채팅방에 포함될 수 없습니다.", HttpStatus.BAD_REQUEST),
    CHATROOM_NOT_FOUND(1004, "채팅방이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    CHATROOM_FORBIDDEN(1005, "해당 채팅방에 접근할 권한이 없습니다.", HttpStatus.FORBIDDEN),

    // 채팅 메시지 관련 실패
    TEXT_MISSING(1100, "텍스트 메시지가 비어있습니다.", HttpStatus.BAD_REQUEST),
    TOO_LONG_TEXT(1101, "텍스트 메시지가 길이 제한을 초과했습니다.", HttpStatus.BAD_REQUEST),
    FILE_MISSING(1102, "파일이 필요합니다.", HttpStatus.BAD_REQUEST),
    MESSAGE_TYPE_MISSING(1103, "메시지 타입이 필요합니다.", HttpStatus.BAD_REQUEST),
    FILE_NOT_ALLOWED(1104, "텍스트 메시지에는 파일이 포함될 수 없습니다.", HttpStatus.BAD_REQUEST),
    FILE_KEY_MISSING(1105, "파일 key가 필요합니다.", HttpStatus.BAD_REQUEST),
    TEXT_NOT_ALLOWED(1106, "파일 메시지에는 텍스트가 포함될 수 없습니다.", HttpStatus.BAD_REQUEST),

    // 유효성 검사 실패
    VALIDATION_FAILED(40000, "유효하지 않은 입력입니다.", HttpStatus.BAD_REQUEST),

    // Feign 통신 실패
    FEIGN_COMMUNICATION_ERROR(50000, "내부 서비스 통신 중 오류가 발생했습니다.", HttpStatus.BAD_GATEWAY),
    FEIGN_BAD_REQUEST(50001, "내부 서비스로 잘못된 요청이 전달되었습니다.", HttpStatus.BAD_REQUEST),
    FEIGN_UNAUTHORIZED(50002, "내부 서비스 인증에 실패했습니다.", HttpStatus.UNAUTHORIZED),
    FEIGN_FORBIDDEN(50003, "내부 서비스 접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    FEIGN_NOT_FOUND(50004, "내부 서비스에서 요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    FEIGN_INTERNAL_SERVER_ERROR(50005, "내부 서비스에서 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FEIGN_UNKNOWN_ERROR(50006, "내부 서비스와의 통신 중 알 수 없는 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final int httpStatusCode;

    ResponseDtoStatus(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatus.value();
    }

}
