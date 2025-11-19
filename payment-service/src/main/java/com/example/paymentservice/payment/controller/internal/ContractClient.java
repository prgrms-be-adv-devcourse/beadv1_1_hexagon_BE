package com.example.paymentservice.payment.controller.internal;

import com.example.paymentservice.common.dto.ResponseDto;
import com.example.paymentservice.payment.controller.dto.request.DepositRechargeRequest;
import com.example.paymentservice.payment.controller.dto.response.DepositRechargeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "contract-service")
public interface ContractClient {
    @PostMapping("/internal/deposits")
    ResponseDto<DepositRechargeResponse> recharge(@RequestBody DepositRechargeRequest request);
}


