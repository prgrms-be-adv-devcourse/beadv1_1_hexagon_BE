package com.example.contractservice.deposit.controller;

import com.example.contractservice.deposit.common.GetDepositApi;
import com.example.contractservice.deposit.common.GetDepositHistoriesApi;
import com.example.contractservice.deposit.controller.dto.response.DepositHistoryCursorResponse;
import com.example.contractservice.deposit.controller.dto.response.DepositInfoResponse;
import com.example.contractservice.deposit.service.DepositService;
import com.example.contractservice.deposit.service.dto.request.DepositHistoryCursorRequest;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deposits")
public class DepositController {
    private final DepositService depositService;

    @GetDepositApi
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<DepositInfoResponse> getMyDeposit(@RequestHeader(name = "X-CODE") String xCode) {
        return ResponseDto.success(depositService.getMyDeposit(xCode));
    }

    @GetDepositHistoriesApi
    @GetMapping("/histories")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<DepositHistoryCursorResponse> getMyDepositHistories(
        @RequestHeader(value = "X-CODE") String xCode,
        @RequestParam(value = "cursor-date", required = false) Instant cursorDate,
        @RequestParam(value = "cursor-code", required = false) String cursorCode) {
        DepositHistoryCursorRequest cursorRequest = new DepositHistoryCursorRequest(xCode, cursorDate,
                cursorCode);

        return ResponseDto.success(depositService.getDepositHistories(cursorRequest));
    }
}
