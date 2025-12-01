package com.example.paymentservice.payment.controller;

import com.example.paymentservice.payment.controller.dto.response.PayRechargeResponse;
import com.example.paymentservice.payment.controller.dto.response.PaymentGetResponse;
import com.example.paymentservice.payment.controller.dto.response.PaymentsGetResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Payment API", description = "결제(충전) API 명세")
public interface PaymentApi {

    @Operation(
            summary = "충전 결제 요청",
            description = "X-CODE 헤더와 금액(amount)을 기반으로 충전 결제를 요청합니다."
    )
    @Parameters({
            @Parameter(name = "X-CODE", description = "사용자 인증용 코드 (헤더)", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "amount", description = "충전할 금액", required = true, example = "1000")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 요청 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 코드 또는 금액)"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (X-CODE 헤더 없음 또는 유효하지 않음)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    ResponseEntity<ResponseDto<PayRechargeResponse>> payForRecharge(
            @RequestHeader(name = "X-CODE") String code,
            @PathVariable String amount
    );

    @Operation(summary = "결제 목록 조회", description = "모든 결제 목록을 조회합니다.")
    @Parameters({
            @Parameter(name = "X-CODE", description = "사용자 인증용 코드 (헤더)", required = true, in = ParameterIn.HEADER)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    ResponseEntity<ResponseDto<PaymentsGetResponse>> getAllPayments(@RequestHeader(name = "X-CODE") String code);

    @Operation(summary = "결제 상세 조회", description = "특정 주문 코드(orderId)를 기준으로 결제 상세 정보를 조회합니다.")
    @Parameters({
            @Parameter(name = "X-CODE", description = "사용자 인증용 코드 (헤더)", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "order-code", description = "조회할 주문 코드(orderId)", required = true, example = "123e4567-e89b-12d3-a456-426614174000", in = ParameterIn.PATH)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 주문 결제 정보 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    ResponseEntity<ResponseDto<PaymentGetResponse>> getPaymentByOrderCode(@RequestHeader(name = "X-CODE") String code,
            @PathVariable("order-pg-id") String orderPgId);
}

