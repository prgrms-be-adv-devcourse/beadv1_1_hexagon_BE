package com.example.cartpostservice.commissions.controller;

import com.example.cartpostservice.commissions.controller.dto.request.CommissionUpsertRequest;
import com.example.cartpostservice.commissions.controller.dto.response.CommissionCreateResponse;
import com.example.cartpostservice.commissions.controller.dto.response.CommissionElementReadResponse;
import com.example.cartpostservice.commissions.controller.dto.response.CommissionUpdateResponse;
import com.example.cartpostservice.commissions.controller.dto.response.CommissionReadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Commissions API", description = "의뢰글 API 명세")
public interface CommissionsApi {

    @Operation(summary = "의뢰글 생성", description = "X-CODE 헤더를 기준으로 의뢰글을 생성합니다.")
    @Parameters({
            @Parameter(name = "X-CODE", description = "요청 사용자 식별 코드", required = true, in = ParameterIn.HEADER)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "의뢰글 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    ResponseEntity<ResponseDto<CommissionCreateResponse>> createCommission(@RequestHeader("X-CODE") String code,
            CommissionUpsertRequest commissionUpsertRequest);

    @Operation(summary = "의뢰글 조회", description = "의뢰글 코드를 기준으로 의뢰글을 조회합니다.")
    @Parameter(name = "commissionsCode", description = "의뢰글 코드", required = true)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 의뢰글을 찾을 수 없음")
    })
    ResponseEntity<ResponseDto<CommissionElementReadResponse>> readCommission(@PathVariable String commissionCode);

    @Operation(summary = "의뢰글 업데이트", description = "X-CODE 헤더와 의뢰글 코드를 기준으로 의뢰글을 수정합니다.")
    @Parameters({
            @Parameter(name = "X-CODE", description = "요청 사용자 식별 코드", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "commissionsCode", description = "수정할 의뢰글 코드", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "대상 의뢰글 없음")
    })
    ResponseEntity<ResponseDto<CommissionUpdateResponse>> updateCommission(
            @RequestHeader("X-CODE") String code,
            @PathVariable String commissionCode,
            @RequestBody CommissionUpsertRequest commissionUpsertRequest
    );

    @Operation(summary = "의뢰글 삭제", description = "X-CODE 헤더와 의뢰글 코드를 기준으로 의뢰글을 삭제합니다.")
    @Parameters({
            @Parameter(name = "X-CODE", description = "요청 사용자 식별 코드", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "commissionsCode", description = "삭제할 의뢰글 코드", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "대상 의뢰글 없음")
    })
    ResponseEntity<ResponseDto<Empty>> deleteCommission(
            @RequestHeader("X-CODE") String code,
            @PathVariable String commissionCode
    );


    @Operation(summary = "의뢰글 마감", description = "X-CODE 헤더를 기준으로 의뢰글을 마감 처리합니다.")
    @Parameter(name = "X-CODE", description = "요청 사용자 식별 코드", required = true, in = ParameterIn.HEADER)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "마감 성공"),
            @ApiResponse(responseCode = "404", description = "대상 의뢰글 없음")
    })
    ResponseEntity<ResponseDto<Empty>> finishCommission(
            @RequestHeader("X-CODE") String code,
            @PathVariable String commissionCode
    );


    @Operation(summary = "내 의뢰글 목록 조회", description = "X-CODE 헤더를 기준으로 본인의 의뢰글 목록을 페이징 조회합니다.")
    @Parameters({
            @Parameter(name = "X-CODE", description = "사용자 식별 코드", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0"),
            @Parameter(name = "size", description = "페이지 크기", example = "10"),
            @Parameter(name = "sort", description = "정렬 기준 (예: createdAt,desc)", example = "isOpen,asc")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<ResponseDto<Page<CommissionReadResponse>>> readOwnCommissions(
            @RequestHeader("X-CODE") String code,
            Pageable pageable
    );

    @Operation(summary = "커미션 종료", description = "특정 커미션을 종료 처리합니다.")
    @Parameters({
            @Parameter(name = "X-CODE", description = "사용자 코드", required = true, example = "USER123", in = ParameterIn.HEADER),
            @Parameter(name = "commissionsCode", description = "종료할 커미션 코드", required = true, example = "COM123", in = ParameterIn.PATH)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 종료"),
            @ApiResponse(responseCode = "404", description = "커미션을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<ResponseDto<Empty>> canAccessCommission(
            @RequestHeader("X-CODE") String code,
            @PathVariable String commissionCode
    );
}
