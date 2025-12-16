package com.example.profileservice.common.model.vo.util;

import org.hexagon.core.dto.ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CONTRACT-SERVICE", url = "http://CONTRACT-SERVICE")
public interface ContractFeignClient {

    /**
     * 특정 계약 정보를 상세 조회
     * @param contractCode 계약 고유 코드
     * @return ContractInfo를 담은 응답 DTO
     */
    @GetMapping("/internal/contracts/{contractCode}")
    ResponseDto<ContractInfo> getContractByCode(@PathVariable String contractCode);

    /**
     * 특정 회원이 해당 계약을 이미 평가했는지 확인
     * @param contractCode 계약 고유 코드
     * @param memberCode 평가를 시도하는 회원 코드
     * @return 이미 평가했으면 true, 아니면 false
     */
    @GetMapping("/internal/contracts/{contractCode}/is-rated-by/{memberCode}")
    ResponseDto<Boolean> isContractRatedBy(@PathVariable String contractCode, @PathVariable String memberCode);
}