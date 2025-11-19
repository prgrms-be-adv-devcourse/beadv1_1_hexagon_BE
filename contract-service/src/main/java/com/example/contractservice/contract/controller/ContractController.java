package com.example.contractservice.contract.controller;

import com.example.contractservice.common.ResponseDto;
import com.example.contractservice.common.PaymentType;
import com.example.contractservice.contract.common.Order;
import com.example.contractservice.contract.common.swagger.annotation.ContractCancelApi;
import com.example.contractservice.contract.common.swagger.annotation.ContractConfirmApi;
import com.example.contractservice.contract.common.swagger.annotation.ContractCreateApi;
import com.example.contractservice.contract.common.swagger.annotation.GetContractByCodeApi;
import com.example.contractservice.contract.common.swagger.annotation.GetContractsApi;
import com.example.contractservice.contract.controller.dto.request.ContractCreateRequest;
import com.example.contractservice.contract.controller.dto.response.ContractCreateResponse;
import com.example.contractservice.contract.controller.dto.response.ContractDetailResponse;
import com.example.contractservice.contract.controller.dto.response.ContractInfoResponse;
import com.example.contractservice.contract.controller.dto.response.ContractListWithCursorResponse;
import com.example.contractservice.contract.service.ContractReadService;
import com.example.contractservice.contract.service.ContractService;
import com.example.contractservice.contract.service.dto.request.ContractConfirmRequest;
import com.example.contractservice.contract.service.dto.request.ContractDetailRequest;
import com.example.contractservice.contract.service.dto.request.ContractReadCursorRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contracts")
public class ContractController {
    private static final int DAYS_OF_MONTH = 30;

    private final ContractService contractService;
    private final ContractReadService contractReadService;

    @GetContractsApi
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ContractListWithCursorResponse getContracts(@RequestHeader(name = "X-CODE") String xCode,
            @RequestParam(value = "cursor-date", required = false) Instant cursorDate,
            @RequestParam(value = "cursor-code", required = false) String cursorCode,
            @RequestParam(value = "order", required = false, defaultValue = "desc") String order) {

        return contractReadService.findAllBy(new ContractReadCursorRequest(xCode, cursorDate, cursorCode, getOrder(order)));
    }

    @GetContractByCodeApi
    @GetMapping("/{code}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<ContractDetailResponse> getContractByCode(@RequestHeader(name = "X-CODE") String xCode,
            @PathVariable String code) {

        ContractDetailResponse detailResponse = contractReadService.findDetailBy(new ContractDetailRequest(xCode, code));

        return ResponseDto.ok(detailResponse);
    }

    @ContractCreateApi
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto<ContractCreateResponse> requestContract(@RequestHeader(name = "X-CODE") String xCode,
            @RequestBody ContractCreateRequest request) {

        validateCreateRequest(xCode, request);

        return ResponseDto.ok(contractService.requestContract(request));
    }

    @ContractConfirmApi
    @PostMapping("/{code}/confirm")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<ContractInfoResponse> confirmContract(@RequestHeader(name = "X-CODE") String xCode,
            @PathVariable String code) {

        ContractConfirmRequest request = new ContractConfirmRequest(xCode, code);

        return ResponseDto.ok(contractService.confirmContract(request));
    }

    @ContractCancelApi
    @PostMapping("/{code}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ContractInfoResponse cancelContract(@RequestHeader(name = "X-CODE") String xCode,
            @PathVariable String code) {

        return new ContractInfoResponse(UUID.randomUUID().toString(), "CANCELLED");
    }

    private void validateCreateRequest(String xCode, ContractCreateRequest request) {
        if (!canCreateContract(xCode, request)) {
            throw new IllegalArgumentException("X-CODE 회원 코드는 요청 회원 코드 혹은 요청 성립 회원 코드와 일치해야 합니다.");
        }

        if (request.requestorCode().equals(request.contractorCode())) {
            throw new IllegalArgumentException("자기 자신과 계약할 수 없습니다.");
        }

        if (!(request.contractorCode().equals(request.freelancerCode()) || request.requestorCode().equals(request.freelancerCode()))) {
            throw new IllegalArgumentException("계약 요청자, 성립자 코드 중 하나는 반드시 계약 상 프리랜서 코드와 일치해야 합니다.");
        }

        if (request.startedAt().isAfter(request.endedAt()) || request.startedAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("프로젝트 일자 설정이 잘못되었습니다.");
        }

        long projectDays = Duration.between(request.startedAt(), request.endedAt()).toDays();

        boolean isMonthly = request.paymentType().equals(PaymentType.MONTHLY.name());

        if (projectDays < DAYS_OF_MONTH && isMonthly) {
            throw new IllegalArgumentException("프로젝트 기간이 짧아 월급 단위 금액으로 생성할 수 없습니다.");
        }
    }

    private boolean canCreateContract(String xCode, ContractCreateRequest request) {
        return request.contractorCode().equals(xCode) || request.requestorCode().equals(xCode);
    }

    private Order getOrder(String order) {
        return switch (order.toLowerCase()) {
            case "asc" -> Order.ASC;
            case "desc" -> Order.DESC;
            default -> throw new IllegalArgumentException("잘못된 정렬 방향입니다.");
        };
    }

}
