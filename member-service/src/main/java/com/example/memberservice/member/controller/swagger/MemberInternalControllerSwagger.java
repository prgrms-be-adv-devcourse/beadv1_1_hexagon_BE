package com.example.memberservice.member.controller.swagger;

import com.example.memberservice.common.exception.ErrorCode;
import com.example.memberservice.common.swagger.annotation.ApiErrorResponses;
import org.hexagon.core.dto.ResponseDto;
import com.example.memberservice.member.service.model.dto.output.MemberExistOutput;
import com.example.memberservice.member.service.model.dto.output.MemberInfoOutput;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;


@Tag(name = "Member Internal API", description = "사용자 관련 Internal API를 제공합니다.")
public interface MemberInternalControllerSwagger {

    @Operation(summary = "사용자 정보 조회", description = "회원 코드를 기반으로 사용자 정보를 조회합니다.(최대 2개)")
    @Parameters({
        @Parameter(name = "member-code", description = "조회할 사용자 코드", in = ParameterIn.QUERY, required = false)
    })
    @ApiErrorResponses(exceptions = ErrorCode.INTERNAL_ILLEGAL_MEMBER_CODE)
    ResponseDto<MemberInfoOutput> getMemberInfoByCode(
        @RequestParam(name = "member-code", required = false) List<String> paramMemberCode
    );

    @Operation(summary = "사용자 존재 여부 확인", description = "회원 코드 기반으로 사용자가 존재하는지 확인합니다.")
    @Parameter(name = "member-code", description = "존재하는 지 조회할 사용자 코드", in = ParameterIn.QUERY, required = false)
    ResponseDto<MemberExistOutput> existMemberByCode(
        @RequestParam(name = "member-code", required = false) List<String> paramMemberCode
    );
}
