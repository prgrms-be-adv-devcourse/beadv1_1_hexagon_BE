package com.example.contractservice.common.util.feign;

import com.example.contractservice.contract.service.dto.response.CommissionRecruitmentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "commissionClient", path = "internal/commissions")
public interface CommissionClient {

    @GetMapping("/recruitment-status/{commission-code}")
    CommissionRecruitmentResponse getRecruitmentStatus(@PathVariable(name = "commission-code") String commissionCode);
}
