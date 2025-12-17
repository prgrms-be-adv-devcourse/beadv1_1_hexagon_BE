package com.example.contractservice.common.util.feign;

import org.hexagon.core.dto.ResponseDto;
import com.example.contractservice.contract.service.dto.response.CommissionRecruitmentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cartpost-service", path = "/internal/commissions")
public interface CommissionClient {

    @GetMapping("/recruitment-status/{commission-code}")
    ResponseDto<CommissionRecruitmentResponse> getRecruitmentStatus(@PathVariable(name = "commission-code") String commissionCode);
}
