package com.example.memberservice.auth.email.controller.swagger;


import com.example.memberservice.auth.email.controller.model.dto.CreateEmailAuthRequest;
import com.example.memberservice.common.exception.ErrorCode;
import com.example.memberservice.common.swagger.annotation.ApiErrorResponses;
import com.example.memberservice.member.model.enums.MemberRole;
import jakarta.validation.Valid;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Email 인증 관련 API", description = "이메일 인증 관련 API를 제공합니다.")
public interface EmailAuthApiControllerSwagger {
    @Operation(summary = "이메일 인증 코드 전송", description = "사용자 이메일로 인증 코드를 전송합니다.")
    @Parameters({
        @Parameter(name = "role", description = "사용자 역할 (FREELANCER, CLIENT)", in = ParameterIn.PATH, required = true),
        @Parameter(name = "X-CODE", description = "로그인 사용자 코드", in = ParameterIn.HEADER, required = true)
    })
    @ApiErrorResponses(exceptions = {
        ErrorCode.EMAIL_VERIFICATION_BAD_ROLE_REQUEST,
        ErrorCode.MEMBER_NOT_FOUND,
        ErrorCode.VALIDATION_FAILED,
        ErrorCode.INTERNAL_SERVER_ERROR,
        ErrorCode.DATA_SAVE_FAILED,
    })
    ResponseDto<Empty> sendEmailAuthCode(
        @PathVariable MemberRole role,
        @RequestHeader(name = "X-CODE") String memberCode,
        @RequestBody @Valid CreateEmailAuthRequest request
    );


    // ----------------------
    // 이메일 인증 코드 검증 API
    // ----------------------
    @Operation(summary = "이메일 인증 코드 검증", description = "이메일로 전송된 인증 코드를 검증합니다.")
    @Parameters({
        @Parameter(name = "role", description = "사용자 역할 (FREELANCER, CLIENT)", in = ParameterIn.PATH, required = true),
        @Parameter(name = "X-CODE", description = "로그인 사용자 코드", in = ParameterIn.HEADER, required = true),
        @Parameter(name = "code", description = "확인할 인증 코드 값", in = ParameterIn.QUERY, required = true)
    })
    @ApiErrorResponses(exceptions = {
        ErrorCode.EMAIL_VERIFICATION_BAD_ROLE_REQUEST,
        ErrorCode.EMAIL_VERIFICATION_NOT_FOUND,
        ErrorCode.VALIDATION_FAILED,
        ErrorCode.INTERNAL_SERVER_ERROR,
        ErrorCode.DATA_SAVE_FAILED,
        ErrorCode.MEMBER_NOT_FOUND,
        ErrorCode.MAIL_SEND_FAILED,
    })
    ResponseDto<Empty> verifyEmailAuthCode(
        @PathVariable MemberRole role,
        @RequestHeader(name = "X-CODE") String memberCode,
        @RequestParam(name = "code") String authCode
    );

}
