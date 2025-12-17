package com.example.contractservice.contract.controller.dto.response;

import com.example.contractservice.contract.domain.Contract;
import com.example.contractservice.contract.domain.vo.ContractContent;
import com.example.contractservice.contract.domain.vo.ContractInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public record ContractDetailResponse(
    @Schema(description = "클라이언트 회원 이름", example = "김회원")
    String clientName,
    @Schema(description = "프리랜서 회원 이름", example = "프리랜서")
    String freelancerName,
    @Schema(description = "계약 생성 일시", example = "2023-08-31T01:07:25.295Z")
    Instant createdAt,
    @Schema(description = "프로젝트 시작일", example = "2023-08-31T01:07:25.295Z")
    Instant startedAt,
    @Schema(description = "프로젝트 종료일", example = "2023-08-31T01:07:25.295Z")
    Instant endedAt,
    @Schema(description = "지불 방식", allowableValues = {"ONE_TIME", "MONTHLY"}, example = "MONTHLY")
    String paymentType,
    @Schema(description = "계약 상태",
        allowableValues = {"REQUESTED", "CONFIRMED", "PAID", "IN_PROGRESS", "DONE"},
        example = "PAID")
    String status,
    @Schema(description = "계약 명", example = "계약1")
    String name,
    @Schema(description = "계약 내용", example = "내용")
    String body
) {

    public static ContractDetailResponse of(Contract contract, String clientName, String freelancerName) {
        ContractInfo contractInfo = contract.getInfo();
        ContractContent contractContent = contract.getContent();

        return new ContractDetailResponse(
                clientName,
                freelancerName,
                contract.getCreatedAt(),
                contractInfo.startedAt(),
                contractInfo.endedAt(),
                contractInfo.paymentType().name(),
                contractInfo.status().name(),
                contractContent.name(),
                contractContent.body()
        );
    }
}
