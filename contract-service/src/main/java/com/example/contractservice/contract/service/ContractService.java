package com.example.contractservice.contract.service;

import static com.example.contractservice.contract.domain.exception.ContractErrorCode.*;

import com.example.contractservice.common.domain.exception.DomainException;
import com.example.contractservice.common.util.feign.CommissionClient;
import com.example.contractservice.common.util.feign.MemberClient;
import com.example.contractservice.contract.controller.dto.request.ContractCancelRequest;
import com.example.contractservice.contract.controller.dto.request.ContractCreateRequest;
import com.example.contractservice.contract.controller.dto.response.ContractBriefWithNicknameResponse;
import com.example.contractservice.contract.controller.dto.response.ContractCreateResponse;
import com.example.contractservice.contract.controller.dto.response.ContractPayResponse;
import com.example.contractservice.contract.domain.Contract;
import com.example.contractservice.contract.domain.exception.ContractException;
import com.example.contractservice.contract.repository.ContractRepository;
import com.example.contractservice.contract.service.dto.request.ContractPayProcessRequest;
import com.example.contractservice.contract.service.dto.request.ContractPayServiceRequest;
import com.example.contractservice.contract.service.dto.response.MemberInfoResponse;
import com.example.contractservice.contract.service.dto.response.MemberInfoResponse.MemberInfo;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractService {
    private static final int CONTRACT_MEMBER_NUM = 2;

    private final MemberClient memberClient;
    private final CommissionClient commissionClient;
    private final ContractRepository contractRepository;
    private final ContractPayService contractPayService;
    private final ContractCancelService contractCancelService;

    public List<ContractBriefWithNicknameResponse> getBriefInfos(List<String> codes) {
        // 코드를 기반으로 모든 Contract를 한 번에 조회
        List<Contract> contracts = contractRepository.findAllByCodes(codes);

        if (contracts.isEmpty()) { // 없다면 조기 종료로 네트워크 통신 방지
            return Collections.emptyList();
        }

        // 계약 목록에서 클라이언트, 프리랜서 code 수집
        Set<String> memberCodes = contracts.stream()
                .flatMap(contract -> Stream.of(contract.getInfo().clientCode(), contract.getInfo().freelancerCode()))
                .collect(Collectors.toSet());

        // member 모듈로부터 정보 가져오기
        List<MemberInfo> memberInfos = Optional.ofNullable(memberClient.getMemberInfo(memberCodes.stream().toList()))
                .orElseThrow(() -> new ContractException(INVALID_MEMBER))
                .members();
        Map<String, String> membersByCode = memberInfos.stream()
                .collect(Collectors.toMap(MemberInfo::code, MemberInfo::name)); // code별로 info 분류

        return contracts.stream()
                .map(contract -> convertToBriefResponse(contract, membersByCode))
                .toList();
    }

    @Transactional
    public ContractCreateResponse requestContract(ContractCreateRequest request) {
        isValidMember(request.clientCode(), request.freelancerCode());
        isCommissionOpen(request.commissionCode());

        Contract createdContract = request.toContract();

        Contract contract = contractRepository.saveContract(createdContract);

        return ContractCreateResponse.of(contract.getCode());
    }

    /** 계약 코드를 받아 결제를 수행합니다. 다음 단계로 수행될 수 있습니다. <br />
     * 1. 계약들을 리포지터리에서 가져옵니다. <br />
     * 2. 계약들을 하나씩 확인하며 결제를 합니다. <br />
     * &nbsp 2-1. 의뢰글 마감 카프카 이벤트가 발행될 수 있습니다. <br />
     * &nbsp 2-2. 정산 데이터가 삽입됩니다.
     * <br />
     *
     * @param request 계약 결제를 위한 정보를 담는 DTO
     * @return 결제에 성공/실패한 계약 정보
     */
    public ContractPayResponse payContracts(ContractPayServiceRequest request) {
        Map<Boolean, List<ContractPayProcessRequest>> resultSet = request.contractCodes()
                .stream()
                .map(contractCode -> new ContractPayProcessRequest(request.xCode(), contractCode))
                .collect(Collectors.partitioningBy(this::pay));

        List<String> success = resultSet.get(true).stream().map(ContractPayProcessRequest::contractCode).toList();
        List<String> fail = resultSet.get(false).stream().map(ContractPayProcessRequest::contractCode).toList();

        return new ContractPayResponse(success, fail);
    }

    public void cancelContract(ContractCancelRequest request) {
        Contract contract = contractRepository.findByCode(request.contractCode());

        validateCancelRequest(request.xCode(), contract);

        contractCancelService.processCancel(contract);
    }

    private void isCommissionOpen(String commissionCode) {
        boolean isOpen = Optional.ofNullable(commissionClient.getRecruitmentStatus(commissionCode))
                .orElseThrow(() -> new ContractException(COMMISSION_NOT_AVAILABLE))
                .isOpen();

        if (!isOpen) {
            throw new ContractException(COMMISSION_NOT_AVAILABLE);
        }
    }

    private void validateCancelRequest(String xCode, Contract contract) {
        if (!contract.isRelatedWith(xCode)) {
            throw new ContractException(MEMBER_NOT_RELATED);
        }
    }

    private ContractBriefWithNicknameResponse convertToBriefResponse(Contract contract,
            Map<String, String> membersByCode) {
        return ContractBriefWithNicknameResponse.of(
                contract,
                membersByCode.get(contract.getInfo().clientCode()),
                membersByCode.get(contract.getInfo().freelancerCode())
        );
    }

    private void isValidMember(String clientCode, String freelancerCode) {
        MemberInfoResponse memberInfoResponse = Optional.ofNullable(memberClient.getMemberInfo(List.of(clientCode, freelancerCode)))
                .orElseThrow(() -> new ContractException(INVALID_MEMBER));

        List<MemberInfo> memberInfos = memberInfoResponse.members();

        if (memberInfos.size() != CONTRACT_MEMBER_NUM) {
            throw new ContractException(INVALID_MEMBER);
        }

        MemberInfo freelancerInfo = memberInfos.stream()
                .filter(memberInfo -> memberInfo.code().equals(freelancerCode))
                .findAny().orElseThrow(() -> new ContractException(INVALID_MEMBER));

        if (!freelancerInfo.canWork()) {
            throw new ContractException(NOT_FREELANCER);
        }
    }

    /** 실제 결제 로직. 처리 중 예외 발생 시, 실패 결제로 처리되며 로깅합니다.
     *
     * @param request 결제를 위한 DTO
     * @return 메서드 실행 성공 여부
     */
    private boolean pay(ContractPayProcessRequest request) {
        try {
            contractPayService.processPayment(request);
        } catch (DomainException e) {
            log.warn("계약 코드 {}에 대하여 다음 사유로 결제 처리가 불가능합니다. 사유: {}", request.contractCode(), e.getErrorCode().getMessage());

            return false;
        } catch (Exception e) {
            log.error("계약 코드 {}에 대하여 다음 사유로 결제 처리가 불가능합니다. 사유: ", request.contractCode(), e);

            return false;
        }

        return true;
    }

}
