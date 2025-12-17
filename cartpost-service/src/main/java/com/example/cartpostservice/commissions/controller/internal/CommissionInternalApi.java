package com.example.cartpostservice.commissions.controller.internal;

import com.example.cartpostservice.commissions.controller.external.dto.response.CommissionElementReadResponse;
import com.example.cartpostservice.commissions.controller.internal.dto.response.CommissionRecruitmentStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Commissions Internal API", description = "의뢰글 Internal API 명세")
@Validated
public interface CommissionInternalApi {

    @Operation(summary = "의뢰글 마감 상태 반환", description = "의뢰글의 마감 상태를 요청하는 기능입니다.")
    @Parameter(
            description = "조회할 의뢰글의 code",
            schema = @Schema(description = "조회하고 싶은 의뢰글 코드를 전달해 주세요")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "의뢰글 생성 성공"),
    })
    ResponseEntity<ResponseDto<CommissionRecruitmentStatusResponse>> getRecruitmentStatus(
            @PathVariable String commissionCode);

    @Operation(summary = "의뢰글 상세 내역 반환", description = "의뢰글의 상세 내역을 내부 모듈 요청을 위해 반환 하는 기능입니다.")
    @Parameter(
            description = "조회할 의뢰글의 code",
            schema = @Schema(description = "조회하고 싶은 의뢰글 코드를 전달해 주세요")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "의뢰글 생성 성공"),
    })
    ResponseEntity<ResponseDto<CommissionElementReadResponse>> readCommissionDetail(
            @PathVariable(name = "commission-code") String commissionCode);
}
