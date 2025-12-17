package com.example.recommendationservice.client;

import org.hexagon.core.dto.ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

// name: 호출 대상 서비스 이름, path: 기본 경로
@FeignClient(
    name = "contract-service",
    path = "/internal/contracts"
)
public interface ContractServiceClient {

    // 의뢰글에 지원한 프리랜서 코드 목록 조회
    @GetMapping("/commissions/{commission-code}/freelancer")
    ResponseDto<List<String>> getAppliedFreelancerCodes(
        @PathVariable("commission-code") String commissionCode
    );

}
