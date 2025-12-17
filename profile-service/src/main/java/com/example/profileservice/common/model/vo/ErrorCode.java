package com.example.profileservice.common.model.vo;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 4xx 클라이언트 에러 - 일반 (3000 ~ 3099)
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, 3001, "입력 값이 유효하지 않습니다."),
    NOT_FOUND_RESOURCE(HttpStatus.NOT_FOUND, 3002, "요청하신 리소스를 찾을 수 없습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, 3003, "접근 권한이 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, 3004, "지원되지 않는 HTTP 메서드입니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, 3005, "유효하지 않은 타입의 값입니다."),
    INVALID_MEMBER_CODE(HttpStatus.BAD_REQUEST, 3006, "유효하지 않은 사용자 코드입니다."),

    // 4xx 클라이언트 에러 - experience (3100 ~ 3199)
    EXPERIENCE_NOT_FOUND(HttpStatus.NOT_FOUND, 3101, "요청하신 경력/경험 항목을 찾을 수 없습니다."),
    UNAUTHORIZED_EXPERIENCE_ACCESS(HttpStatus.FORBIDDEN, 3102, "해당 경력/경험 항목에 대한 접근 권한이 없습니다."),

    // 4xx 클라이언트 에러 - rating (3200 ~ 3299)
    RATING_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, 3201, "평가 대상 회원을 찾을 수 없습니다."),
    CANNOT_RATE_MYSELF(HttpStatus.BAD_REQUEST, 3202, "자기 자신을 평가할 수 없습니다."),
    CONTRACT_NOT_FOUND(HttpStatus.BAD_REQUEST, 3203, "계약 코드가 유효하지 않거나 찾을 수 없습니다."),
    CONTRACT_NOT_COMPLETED(HttpStatus.BAD_REQUEST, 3204, "계약 상태가 DONE이 아니어서 평가할 수 없습니다."),
    UNAUTHORIZED_RATING_ACCESS(HttpStatus.BAD_REQUEST, 3205, "평가자가 해당 계약의 당사자가 아니거나, 평가 대상이 계약 상대방이 아닙니다."),
    RATING_ALREADY_SUBMITTED(HttpStatus.BAD_REQUEST, 3206, "해당 계약에 대해 이미 평가를 완료했습니다."),
    CONTRACT_FETCH_FAILED(HttpStatus.BAD_REQUEST, 3207, "Contract 모듈 조회 중 통신 오류 발생했습니다."),

    // 4xx 클라이언트 에러 - resume (3300 ~ 3399)
    RESUME_NOT_FOUND(HttpStatus.NOT_FOUND, 3301, "요청하신 이력서를 찾을 수 없습니다."),
    UNAUTHORIZED_RESUME_ACCESS(HttpStatus.FORBIDDEN, 3302, "해당 이력서에 대한 접근 권한이 없습니다."),
    RESUME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, 3303, "이력서가 이미 존재합니다."),

    // 4xx 클라이언트 에러 - selfPromotion (3400 ~ 3499)
    PROMOTION_NOT_FOUND(HttpStatus.NOT_FOUND, 3401, "요청하신 셀프 프로모션 게시글을 찾을 수 없습니다."),
    UNAUTHORIZED_PROMOTION_ACCESS(HttpStatus.FORBIDDEN, 3402, "해당 셀프 프로모션 게시글에 대한 접근 권한이 없습니다."),
    INVALID_RESUME_CODE_LINK(HttpStatus.BAD_REQUEST, 3403, "연결하려는 이력서 코드가 유효하지 않거나 존재하지 않습니다."),
    PROMOTION_ALREADY_EXISTS(HttpStatus.CONFLICT, 3404, "이미 활성 상태의 셀프 프로모션 게시글이 존재합니다."),
    S3_RESOURCE_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 3405, "S3 파일 저장(temp -> permanent) 요청에 실패했습니다."),
    S3_RESOURCE_SYNC_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 3406, "S3 파일 동기화/삭제 요청에 실패했습니다."),
    S3_DOWNLOAD_URL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 3407, "S3 다운로드 URL 생성 요청에 실패했습니다."),

    // 4xx 클라이언트 에러 - Tag (3500 ~ 3599)
    TAG_NOT_FOUND(HttpStatus.NOT_FOUND, 3501, "해당 기술 태그를 찾을 수 없습니다."),
    TAG_ALREADY_EXISTS(HttpStatus.CONFLICT, 3502, "이미 존재하는 기술 태그입니다."),
    MEMBER_TAG_ALREADY_CONNECTED(HttpStatus.CONFLICT, 3503, "이미 연결된 기술 태그입니다."),
    MEMBER_TAG_NOT_FOUND(HttpStatus.NOT_FOUND, 3504, "해제할 기술 태그 연결을 찾을 수 없습니다."),

    // 500 서버 에러 (3900 ~ 3999)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 3901, "서버 내부 오류가 발생했습니다."),
    ;

    private final HttpStatus status;
    private final int code;
    private final String message;

    ErrorCode(final HttpStatus status, final int code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
