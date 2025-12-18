package com.example.cartpostservice.cart.infra.clinet.internal;

import com.example.cartpostservice.cart.infra.clinet.internal.dto.request.ContractPayRequest;
import com.example.cartpostservice.cart.infra.clinet.internal.dto.response.ContractBriefWithNicknameResponse;
import com.example.cartpostservice.cart.infra.clinet.internal.dto.response.ContractPayResponse;
import java.util.List;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "contract-service", path = "/internal/contracts", contextId = "ContractInfoClient")
// 대상 서비스 이름 (Eureka 등록명 등)
public interface ContractClient {

    @PostMapping("/pay")
    ResponseDto<ContractPayResponse> payContract(
            @RequestBody ContractPayRequest request
    );

    @GetMapping("")
    ResponseDto<List<ContractBriefWithNicknameResponse>> getBriefInfo(@RequestParam("code") List<String> codes);
}
