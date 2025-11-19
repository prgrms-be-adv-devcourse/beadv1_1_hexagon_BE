package com.example.contractservice.deposit.controller;

import com.example.contractservice.common.ResponseDto;
import com.example.contractservice.deposit.controller.dto.request.DepositRechargeRequest;
import com.example.contractservice.deposit.controller.dto.response.DepositRechargeResponse;
import com.example.contractservice.deposit.service.DepositService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/deposits")
@RequiredArgsConstructor
public class DepositInternalController {
    private final DepositService depositService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<DepositRechargeResponse> recharge(@RequestBody DepositRechargeRequest request) {

        DepositRechargeResponse rechargeResponse = depositService.recharge(request);

        return ResponseDto.ok(rechargeResponse);
    }
}
