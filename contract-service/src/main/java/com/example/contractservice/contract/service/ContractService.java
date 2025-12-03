package com.example.contractservice.contract.service;

import static com.example.contractservice.contract.domain.exception.ContractErrorCode.*;
import static com.example.contractservice.contract.service.mapper.ContractMapper.*;

import com.example.contractservice.common.UriConstructor;
import com.example.contractservice.common.domain.exception.DomainException;
import com.example.contractservice.contract.controller.dto.request.ContractCreateRequest;
import com.example.contractservice.contract.controller.dto.response.ContractBriefWithNicknameResponse;
import com.example.contractservice.contract.controller.dto.response.ContractCreateResponse;
import com.example.contractservice.contract.controller.dto.response.ContractInfoResponse;
import com.example.contractservice.contract.controller.dto.response.ContractPayResponse;
import com.example.contractservice.contract.domain.Contract;
import com.example.contractservice.contract.domain.exception.ContractException;
import com.example.contractservice.contract.entity.ContractEntity;
import com.example.contractservice.contract.repository.ContractRepository;
import com.example.contractservice.contract.service.dto.request.ContractPayProcessRequest;
import com.example.contractservice.contract.service.dto.request.ContractPayServiceRequest;
import com.example.contractservice.contract.service.dto.response.MemberInfoResponse;
import com.example.contractservice.contract.service.dto.response.MemberInfoResponse.MemberInfo;
import java.net.URI;
import java.util.ArrayList;
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
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractService {
    private static final int CONTRACT_MEMBER_NUM = 2;

    private final ContractRepository contractRepository;
    private final RestTemplate restTemplate;
    private final UriConstructor uriConstructor;
    private final ContractPayService contractPayService;

    public List<ContractBriefWithNicknameResponse> getBriefInfos(List<String> codes) {
        // 코드를 기반으로 모든 ContractEntity를 한 번에 조회
        List<ContractEntity> contractEntities = contractRepository.findAllByCodes(codes);

        if (contractEntities.isEmpty()) { // 없다면 조기 종료로 네트워크 통신 방지
            return Collections.emptyList();
        }

        // 계약 목록에서 클라이언트, 프리랜서 code 수집
        Set<String> memberCodes = contractEntities.stream()
                .flatMap(entity -> Stream.of(entity.getClientCode(), entity.getFreelancerCode()))
                .collect(Collectors.toSet());

        // member 모듈로부터 정보 가져오기
        URI memberInfoUri = uriConstructor.createMemberInfoUrl(memberCodes.stream().toList());
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
        isValidMember(request.clientCode(), request.freelancerCode());

        Contract createdContract = request.toContract();

        ContractEntity contractEntity = contractRepository.saveContract(toEntity(createdContract));

        return ContractCreateResponse.of(contractEntity.getCode());
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
        ArrayList<ContractInfoResponse> success = new ArrayList<>();
        ArrayList<ContractInfoResponse> fail = new ArrayList<>();

        List<ContractEntity> contractEntities = contractRepository.findAllByCodes(request.contractCodes()); // 결제할 계약 코드들
        List<ContractPayProcessRequest> contractPayProcessRequests = contractEntities.stream()
                .map(entity -> new ContractPayProcessRequest(request.xCode(), toDomain(entity), entity))
                .toList();

        contractPayProcessRequests.forEach(payProcessRequest -> pay(payProcessRequest, success, fail));

        return new ContractPayResponse(success, fail);
    }

    private ContractBriefWithNicknameResponse convertToBriefResponse(ContractEntity contractEntity,
            Map<String, String> membersByCode) {
        return ContractBriefWithNicknameResponse.of(
                contractEntity,
                membersByCode.get(contractEntity.getClientCode()),
                membersByCode.get(contractEntity.getFreelancerCode())
        );
    }

    private void isValidMember(String clientCode, String freelancerCode) {
        URI memberInfoUri = uriConstructor.createMemberInfoUrl(List.of(clientCode, freelancerCode));
        MemberInfoResponse memberInfoResponse = Optional.ofNullable(restTemplate.getForObject(memberInfoUri, MemberInfoResponse.class))
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
     * @param success 성공한 결제 정보
     * @param fail 실패한 결제 정보
     */
    private void pay(ContractPayProcessRequest request, List<ContractInfoResponse> success, List<ContractInfoResponse> fail) {
        try {
            contractPayService.processPayment(request);
        } catch (DomainException e) {
            log.warn("계약 코드 {}에 대하여 다음 사유로 결제 처리가 불가능합니다. 사유: {}", request.contract().getCode(), e.getErrorCode().getMessage());

            fail.add(new ContractInfoResponse(request.xCode(), request.contract().getInfo().status().name()));
            return;
        } catch (Exception e) {
            log.error("계약 코드 {}에 대하여 다음 사유로 결제 처리가 불가능합니다. 사유: ", request.contract().getCode(), e);

            fail.add(new ContractInfoResponse(request.xCode(), request.contract().getInfo().status().name()));
            return;
        }

        success.add(new ContractInfoResponse(request.xCode(), request.contract().getInfo().status().name()));
        log.info("정상 처리된 계약 코드: {}", request.contractEntity().getCode());
    }

}
