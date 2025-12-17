package com.example.contractservice.contract.domain.exception;

import com.example.contractservice.common.domain.exception.DomainErrorCode;
import org.springframework.http.HttpStatus;

public class ContractErrorCode extends DomainErrorCode {
    public static final ContractErrorCode NOT_FREELANCER;
    public static final ContractErrorCode INVALID_MEMBER;
    public static final ContractErrorCode DELETED_MEMBER;
    public static final ContractErrorCode MEMBER_NOT_RELATED;

    public static final ContractErrorCode NO_CONTRACT;
    public static final ContractErrorCode NOT_REQUESTED_STATUS;

    public static final ContractErrorCode INVALID_PAYMENT_MEMBER;

    public static final ContractErrorCode INVALID_MEMBER_COUNT;

    public static final ContractErrorCode COMMISSION_CAPACITY_NOT_FOUND;
    public static final ContractErrorCode COMMISSION_SELECTION_COUNT_FULL;
    public static final ContractErrorCode COMMISSION_APPLIED_COUNT_FULL;

    public static final ContractErrorCode CANCEL_NOT_AVAILABLE;
    public static final ContractErrorCode COMMISSION_NOT_AVAILABLE;

    static {
        NOT_FREELANCER = new ContractErrorCode(HttpStatus.BAD_REQUEST, 4000, "프리랜서가 아닙니다. 역할을 확인해 주세요.");
        INVALID_MEMBER = new ContractErrorCode(HttpStatus.BAD_REQUEST, 4001, "적절하지 않은 회원입니다.");
        DELETED_MEMBER = new ContractErrorCode(HttpStatus.BAD_REQUEST, 4002, "탈퇴한 회원은 계약을 생성할 수 없습니다.");
        MEMBER_NOT_RELATED = new ContractErrorCode(HttpStatus.BAD_REQUEST, 4003, "로그인한 회원과 관련된 계약이 아니어서 처리할 수 없습니다.");

        NO_CONTRACT = new ContractErrorCode(HttpStatus.NOT_FOUND, 4010, "해당 계약이 존재하지 않습니다."); // TODO: 계약 코드를 넣을 수 있도록 개선
        NOT_REQUESTED_STATUS = new ContractErrorCode(HttpStatus.BAD_REQUEST, 4012, "요청 상태인 계약만 처리할 수 있습니다.");

        INVALID_PAYMENT_MEMBER = new ContractErrorCode(HttpStatus.BAD_REQUEST, 4020, "현재 로그인한 회원만이 자신의 계약을 결제할 수 있으며 클라이언트여야 합니다.");

        INVALID_MEMBER_COUNT = new ContractErrorCode(HttpStatus.BAD_REQUEST, 4030, "계약 참여자 수를 만족하지 않습니다.");

        COMMISSION_CAPACITY_NOT_FOUND = new ContractErrorCode(HttpStatus.NOT_FOUND, 4040, "해당 의뢰글의 지원 인원, 선정 인원 정보가 존재하지 않습니다.");
        COMMISSION_SELECTION_COUNT_FULL = new ContractErrorCode(HttpStatus.CONFLICT, 4041, "해당 의뢰글에 대한 선정 인원이 꽉 찼습니다.");
        COMMISSION_APPLIED_COUNT_FULL = new ContractErrorCode(HttpStatus.CONFLICT, 4042, "해당 의뢰글의 지원 가능 인원 수가 꽉 찼습니다.");

        CANCEL_NOT_AVAILABLE = new ContractErrorCode(HttpStatus.BAD_REQUEST, 4050, "취소할 수 있는 계약의 상태가 아닙니다.");
        COMMISSION_NOT_AVAILABLE = new ContractErrorCode(HttpStatus.BAD_REQUEST, 4051, "관련 의뢰글이 존재하지 않거나 마감되었습니다.");
    }

    private ContractErrorCode(HttpStatus httpStatusCode, int statusCode, String message) {
        super(httpStatusCode, statusCode, message);
    }

}
