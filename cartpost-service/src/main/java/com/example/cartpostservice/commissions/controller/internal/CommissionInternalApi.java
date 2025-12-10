package com.example.cartpostservice.commissions.controller.internal;

import com.example.cartpostservice.commissions.controller.internal.dto.request.RecruitmentStatusRequest;
import com.example.cartpostservice.commissions.controller.internal.dto.response.CommissionRecruitmentStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Commissions Internal API", description = "의뢰글 Internal API 명세")
public interface CommissionInternalApi {

    @Operation(summary = "의뢰글 마감 상태 반환", description = "의뢰글의 마감 상태를 요청하는 기능입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "의뢰글 생성 성공"),
    })
    ResponseEntity<ResponseDto<CommissionRecruitmentStatusResponse>> getRecruitmentStatus(
            @RequestBody RecruitmentStatusRequest recruitmentStatusRequest);

}
