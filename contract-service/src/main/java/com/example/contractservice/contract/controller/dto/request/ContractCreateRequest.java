package com.example.contractservice.contract.controller.dto.request;

import com.example.contractservice.contract.common.ContractStatus;
import com.example.contractservice.contract.domain.Contract;
import com.example.contractservice.contract.domain.vo.ContractContent;
import com.example.contractservice.contract.domain.vo.ContractInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import org.hexagon.core.vo.PaymentType;

public record ContractCreateRequest(
        @Schema(description = "계약 요청자 회원 코드", example = "8172516b-2076-460f-805d-e60cbc0463c7")
        @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                message = "유효한 UUID 형식이어야 합니다.")
        String requestorCode,
        @Schema(description = "계약 성립자 회원 코드", example = "abdd2b21-d2a1-4d89-8271-e9941e7ef93e")
        @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                message = "유효한 UUID 형식이어야 합니다.")
        String contractorCode,
        @Schema(description = "요청자, 성립자 중 프리랜서인 회원 코드 (정산, 결제에 필요한 항목)", example = "abdd2b21-d2a1-4d89-8271-e9941e7ef93e")
        @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                message = "유효한 UUID 형식이어야 합니다.")
        String freelancerCode,
        @Schema(description = "프로젝트 시작일", example = "2023-08-31T01:07:25.295Z")
        Instant startedAt,
        @Schema(description = "프로젝트 종료일", example = "2023-08-31T01:07:25.295Z")
        Instant endedAt,
        @Schema(description = "지불 방식", allowableValues = {"ONE_TIME", "MONTHLY"}, example = "MONTHLY")
        String paymentType,
        @Schema(description = "단위 금액", example = "10000")
        @Min(value = 1000, message = "계약 단위 금액은 1000원 이상이어야만 합니다.")
        Long unitAmount,
        @Schema(description = "계약 명", example = "계약1")
        @NotBlank(message = "계약 명은 비어있을 수 없습니다.")
        @Size(min = 2, max = 255, message = "계약 명이 너무 짧거나 너무 깁니다.")
        String name,
        @Schema(description = "계약 내용", example = "내용")
        String body
) {

    public Contract toContract() {
        ContractInfo contractInfo = new ContractInfo(requestorCode, contractorCode, freelancerCode, startedAt, endedAt,
                PaymentType.valueOf(paymentType), unitAmount, ContractStatus.REQUESTED);
        ContractContent contractContent = new ContractContent(name, body);
        Instant nowTime = Instant.now();

        return new Contract(contractInfo, contractContent, nowTime, nowTime);
    }
}
