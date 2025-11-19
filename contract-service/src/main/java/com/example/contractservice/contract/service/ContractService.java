package com.example.contractservice.contract.service;

import static com.example.contractservice.contract.domain.exception.ContractErrorCode.*;
import static com.example.contractservice.contract.service.mapper.ContractMapper.*;

import com.example.contractservice.contract.common.ContractStatus;
import com.example.contractservice.contract.controller.dto.request.ContractCreateRequest;
import com.example.contractservice.contract.controller.dto.response.ContractBriefWithNicknameResponse;
import com.example.contractservice.contract.controller.dto.response.ContractCreateResponse;
import com.example.contractservice.contract.controller.dto.response.ContractInfoResponse;
import com.example.contractservice.contract.domain.Contract;
import com.example.contractservice.contract.domain.exception.ContractException;
import com.example.contractservice.contract.domain.vo.ContractInfo;
import com.example.contractservice.contract.entity.ContractEntity;
import com.example.contractservice.contract.repository.ContractRepository;
import com.example.contractservice.contract.service.dto.request.ContractConfirmRequest;
import com.example.contractservice.contract.service.dto.request.ContractPayProcessRequest;
import com.example.contractservice.contract.service.dto.response.MemberInfoResponse;
import com.example.contractservice.contract.service.dto.response.MemberInfoResponse.MemberInfo;
import com.example.contractservice.contract.service.mapper.ContractMapper;
import com.example.contractservice.contract.service.mapper.ContractSettlementMapper;
import com.example.contractservice.deposit.service.dto.request.DepositProcessRequest;
import com.example.contractservice.deposit.service.DepositService;
import com.example.contractservice.settlement.service.SettlementService;
import com.example.contractservice.settlement.service.dto.request.SettlementSaveRequest;
import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.events.contract.ContractEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class ContractService {
    private static final String PAYMENT_COMMENT = "계약 결제";

    private final SettlementService settlementService;
    private final DepositService depositService;
    private final ContractRepository contractRepository;
    private final RestTemplate restTemplate;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Value("${module.member.application.name}")
    private String memberServiceName;

    @Value("${module.member.application.path.member-info}")
    private String memberInfoUrl;

    public List<ContractBriefWithNicknameResponse> getBriefInfos(List<String> codes) {
        // 코드를 기반으로 모든 ContractEntity를 한 번에 조회
        List<ContractEntity> contractEntities = contractRepository.findAllByCodes(codes);

        if (contractEntities.isEmpty()) { // 없다면 조기 종료로 네트워크 통신 방지
            return Collections.emptyList();
        }

        // 계약 목록에서 요청자(requestor)와 계약자(contractor)의 code를 모두 수집 (중복 제거)
        Set<String> memberCodes = contractEntities.stream()
                .flatMap(entity -> Stream.of(entity.getRequestorCode(), entity.getContractorCode()))
                .collect(Collectors.toSet());

        // member 모듈로부터 정보 가져오기
        URI memberInfoUri = createMemberInfoUrl(memberCodes.stream().toList());
        List<MemberInfo> memberInfos = Optional.ofNullable(restTemplate.getForObject(memberInfoUri, MemberInfoResponse.class))
                .orElseThrow(() -> new ContractException(INVALID_MEMBER))
                .members();
        Map<String, String> membersByCode = memberInfos.stream()
                .collect(Collectors.toMap(MemberInfo::code, MemberInfo::name)); // code별로 info 분류

        return contractEntities.stream()
                .map(contractEntity -> convertToBriefResponse(contractEntity, membersByCode))
                .toList();
    }

    @Transactional
    public ContractCreateResponse requestContract(ContractCreateRequest request) {
        isValidMember(List.of(request.requestorCode(), request.contractorCode()));

        Contract createdContract = request.toContract();

        ContractEntity contractEntity = contractRepository.saveContract(toEntity(createdContract));
        Contract savedContract = toDomain(contractEntity);

        return ContractCreateResponse.of(savedContract.getCode());
    }

    @Transactional
    public ContractInfoResponse confirmContract(ContractConfirmRequest request) { // TODO: 동시성 테스트 필요
        ContractEntity contractEntity = contractRepository.findByCode(request.contractCode());
        Contract contract = toDomain(contractEntity);

        validateConfirm(request.xCode(), contract.getInfo());
        contract.confirm();

        ContractMapper.applyToEntity(contract, contractEntity);

        contractRepository.saveContract(contractEntity);

        applicationEventPublisher.publishEvent(new ContractEvent(contract.getCode(), contract.getCreatedAt(), ContractStatus.CONFIRMED.name()));

        return ContractInfoResponse.of(contract.getCode(), contract.getInfo().status().name());
    }

    @Transactional
    public List<ContractInfoResponse> payContracts(ContractPayProcessRequest request) {
        List<ContractEntity> contractEntities = contractRepository.findAllByCodes(request.contractCodes());
        List<Contract> contracts = contractEntities.stream()
                .map(ContractMapper::toDomain)
                .toList();

        validatePayments(request.xCode(), contracts);

        withdrawDeposit(request, contracts);

        changeStatusToPay(contracts, contractEntities);

        saveSettlements(contracts);

        contracts.forEach(contract -> applicationEventPublisher.publishEvent(new ContractEvent(contract.getCode(), contract.getCreatedAt(), ContractStatus.PAID.name())));

        return contracts.stream()
                .map(contract -> ContractInfoResponse.of(contract.getCode(), contract.getInfo().status().name()))
                .toList();
    }

    private ContractBriefWithNicknameResponse convertToBriefResponse(ContractEntity contractEntity,
            Map<String, String> membersByCode) {
        return ContractBriefWithNicknameResponse.of(
                contractEntity,
                membersByCode.get(contractEntity.getRequestorCode()),
                membersByCode.get(contractEntity.getContractorCode())
        );
    }

    private void isValidMember(List<String> memberCodes) {
        URI memberInfoUri = createMemberInfoUrl(memberCodes);
        MemberInfoResponse memberInfoResponse = Optional.ofNullable(restTemplate.getForObject(memberInfoUri, MemberInfoResponse.class))
                .orElseThrow(() -> new ContractException(INVALID_MEMBER));

        List<MemberInfo> memberInfos = memberInfoResponse.members();

        if (memberInfos.size() != memberCodes.size()) {
            throw new ContractException(INVALID_MEMBER);
        }

        if (noFreelancer(memberInfos)) {
            throw new ContractException(NO_FREELANCERS);
        }
    }

    private boolean noFreelancer(List<MemberInfo> memberInfos) {
        return memberInfos.stream()
                .filter(MemberInfo::canWork)
                .findFirst()
                .isEmpty();
    }

    private URI createMemberInfoUrl(List<String> memberCodes) {
        return UriComponentsBuilder.newInstance()
                .scheme("lb")
                .host(memberServiceName)
                .path(memberInfoUrl)
                .queryParam("member-code", memberCodes)
                .build()
                .toUri();
    }

    private void validateConfirm(String xCode, ContractInfo info) {
        isValidMember(List.of(info.requestorCode(), info.contractorCode()));

        if (!xCode.equals(info.contractorCode())) {
            throw new ContractException(NOT_CONTRACTOR);
        }

        if (info.status() != ContractStatus.REQUESTED) {
            throw new ContractException(NOT_REQUESTED_STATUS);
        }
    }

    /**
     * 1. 로그인 사용자가 모든 계약과 연관되어 있는지 확인
     * 2. 클라이언트인지 확인
     */
    private void validatePayments(String xCode, List<Contract> contracts) {
        boolean isValidUser = contracts.stream() // 모든 계약에 대해
                .allMatch(contract -> contract.canUserPay(xCode)); // 로그인 유저가 (계약에 관여) && !(일하는 사람)

        if (!isValidUser) {
            throw new ContractException(INVALID_PAYMENT_MEMBER);
        }

        boolean isAnyNotConfirmed = contracts.stream().anyMatch(contract -> !contract.isConfirmed() // CONFIRMED 상태가 아니거나
                || contract.getInfo().startedAt().isBefore(Instant.now())); // CONFIRMED인데 현재 시간보다 프로젝트 시작일이 이전이라면(실제로는 CANCELLED 상태)

        if (isAnyNotConfirmed) {
            throw new ContractException(NOT_CONFIRMED_STATUS);
        }

    }

    private void changeStatusToPay(List<Contract> contracts, List<ContractEntity> contractEntities) {
        Map<String, ContractEntity> entityMapByCode = contractEntities.stream()
                .collect(Collectors.toMap(ContractEntity::getCode, entity -> entity)); // entity-domain 연결에 사용

        contracts.forEach(Contract::pay);

        contracts.forEach(contract -> {
            ContractEntity entity = entityMapByCode.get(contract.getCode());

            ContractMapper.applyToEntity(contract, entity);
        });

        entityMapByCode.values().forEach(contractRepository::saveContract);
    }

    private void withdrawDeposit(ContractPayProcessRequest request, List<Contract> contracts) {
        Long totalAmount = contracts.stream()
                .map(contract -> contract.getInfo().unitAmount())
                .reduce(0L, Long::sum); // 총 금액

        DepositProcessRequest depositProcessRequest = new DepositProcessRequest(request.xCode(), totalAmount, PAYMENT_COMMENT);
        depositService.process(depositProcessRequest, depositService::withdraw);
    }

    private void saveSettlements(List<Contract> contracts) {
        List<SettlementSaveRequest> settlementSaveRequests = contracts.stream()
                .map(ContractSettlementMapper::toSaveRequest)
                .toList();

        settlementService.savePaidSettlements(settlementSaveRequests);
    }
}
