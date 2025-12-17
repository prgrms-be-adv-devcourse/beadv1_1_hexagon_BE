package com.example.contractservice.contract.controller;

import com.example.contractservice.contract.common.swagger.annotation.CommissionCapacityUpsertApi;
import com.example.contractservice.contract.common.swagger.annotation.ContractPayApi;
import com.example.contractservice.contract.common.swagger.annotation.GetCommissionCapacityApi;
import com.example.contractservice.contract.common.swagger.annotation.GetContractInternalApi;
import com.example.contractservice.contract.controller.dto.request.CommissionsCapacityUpsertRequest;
import com.example.contractservice.contract.controller.dto.request.ContractPayRequest;
import com.example.contractservice.contract.controller.dto.response.CommissionCapacityResponse;
import com.example.contractservice.contract.controller.dto.response.ContractBriefWithNicknameResponse;
import com.example.contractservice.contract.controller.dto.response.ContractPayResponse;
import com.example.contractservice.contract.service.CommissionsCapacityService;
import com.example.contractservice.contract.service.ContractReadService;
import com.example.contractservice.contract.service.ContractService;
import com.example.contractservice.contract.service.dto.request.ContractPayServiceRequest;
import jakarta.validation.Valid;
import com.example.contractservice.contract.controller.dto.response.MemberRoleStatusResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/contracts")
public class ContractInternalController {
    private final ContractService contractService;
    private final CommissionsCapacityService commissionsCapacityService;
    private final ContractReadService contractReadService;

    @ContractPayApi
    @PostMapping("/pay")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<ContractPayResponse> payContract(@Valid @RequestBody ContractPayRequest request) {
        ContractPayServiceRequest serviceRequest = new ContractPayServiceRequest(request.xCode(), request.codes());

        return ResponseDto.success(contractService.payContracts(serviceRequest));
    }

    @GetContractInternalApi
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<List<ContractBriefWithNicknameResponse>> getBriefInfo(@RequestParam(name = "code") List<String> codes) {

        return ResponseDto.success(contractService.getBriefInfos(codes));
    }

    @GetMapping("/{member-code}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<MemberRoleStatusResponse> getMemberRoleStatus(@PathVariable("member-code") String memberCode) {
        return ResponseDto.success(contractService.getMemberRoleStatus(memberCode));
    }

    @GetMapping("/commissions/{commission-code}/freelancer")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<List<String>> getAppliedFreelancerCodes(@PathVariable("commission-code") String commissionCode) {
        return ResponseDto.success(contractReadService.getAppliedFreelancerCodesBy(commissionCode));
    }

    @CommissionCapacityUpsertApi
    @PostMapping("/commissions-capacity")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto<Empty> upsertCapacity(@Valid @RequestBody CommissionsCapacityUpsertRequest request) {
        validateCapacityUpsertRequest(request);

        commissionsCapacityService.upsertCapacity(request);

        return ResponseDto.success();
    }

    @GetCommissionCapacityApi
    @GetMapping("/commissions-capacity/{commission-code}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<CommissionCapacityResponse> getCommissionCapacity(@PathVariable(name = "commission-code") String commissionCode) {
        return ResponseDto.success(commissionsCapacityService.getCapacity(commissionCode));
    }

    private void validateCapacityUpsertRequest(CommissionsCapacityUpsertRequest request) {
        if (request.applyCapacity() < request.selectionCapacity()) {
            throw new IllegalArgumentException("최대 선정 인원이 최대 지원 인원보다 많을 수 없습니다.");
        }
    }

}
