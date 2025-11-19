package com.example.contractservice.contract.service;

import static com.example.contractservice.contract.domain.exception.ContractErrorCode.*;

import com.example.contractservice.common.UriConstructor;
import com.example.contractservice.contract.controller.dto.response.ContractDetailResponse;
import com.example.contractservice.contract.controller.dto.response.ContractListWithCursorResponse;
import com.example.contractservice.contract.domain.exception.ContractException;
import com.example.contractservice.contract.entity.ContractEntity;
import com.example.contractservice.contract.repository.ContractRepository;
import com.example.contractservice.contract.service.dto.request.ContractDetailRequest;
import com.example.contractservice.contract.service.dto.request.ContractReadCursorRequest;
import com.example.contractservice.contract.service.dto.response.MemberInfoResponse;
import com.example.contractservice.contract.service.dto.response.MemberInfoResponse.MemberInfo;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ContractReadService {

    private static final int PAGE_SIZE = 20;
    private static final int CONTRACT_PARTICIPATION_COUNT = 2;

    private final ContractRepository contractRepository;
    private final UriConstructor uriConstructor;
    private final RestTemplate restTemplate;

    public ContractListWithCursorResponse findAllBy(ContractReadCursorRequest request) {
        List<ContractEntity> contractEntities = contractRepository.findAllBy(request.memberCode(), request.cursor(),
                request.cursorCode(), request.order(), PAGE_SIZE);

        return ContractListWithCursorResponse.of(contractEntities, PAGE_SIZE);
    }

    public ContractDetailResponse findDetailBy(ContractDetailRequest request) {
        ContractEntity contractEntity = contractRepository.findByCode(request.contractCode());

        validateMember(request.memberCode(), contractEntity);

        URI memberInfoUrl = uriConstructor.createMemberInfoUrl(Collections.singletonList(request.memberCode()));
        List<MemberInfo> memberInfos = Optional.ofNullable(
                        restTemplate.getForObject(memberInfoUrl, MemberInfoResponse.class))
                .orElseThrow(() -> new ContractException(INVALID_MEMBER)).members();

        if (memberInfos.size() != CONTRACT_PARTICIPATION_COUNT) {
            throw new ContractException(INVALID_MEMBER_COUNT);
        }

        MemberInfo firstMember = memberInfos.get(0);
        MemberInfo secondMember = memberInfos.get(1);

        String requestorName = firstMember.code().equals(contractEntity.getRequestorCode()) ? firstMember.name() : secondMember.name();
        String contractorName = firstMember.code().equals(contractEntity.getRequestorCode()) ? secondMember.name() : firstMember.name();

        return ContractDetailResponse.of(contractEntity, requestorName, contractorName);
    }

    private void validateMember(String memberCode, ContractEntity contractEntity) {
        if (contractEntity.getRequestorCode().equals(memberCode) || contractEntity.getContractorCode().equals(memberCode)) { // 요청자/계약자 중 하나에 속한다면 valid
            return;
        }

        throw new ContractException(INVALID_MEMBER);
    }
}
