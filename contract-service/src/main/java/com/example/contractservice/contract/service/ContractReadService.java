package com.example.contractservice.contract.service;

import static com.example.contractservice.contract.domain.exception.ContractErrorCode.*;

import com.example.contractservice.common.util.feign.MemberClient;
import com.example.contractservice.contract.controller.dto.response.ContractDetailResponse;
import com.example.contractservice.contract.controller.dto.response.ContractListWithCursorResponse;
import com.example.contractservice.contract.domain.Contract;
import com.example.contractservice.contract.domain.exception.ContractException;
import com.example.contractservice.contract.domain.vo.ContractInfo;
import com.example.contractservice.contract.repository.ContractRepository;
import com.example.contractservice.contract.service.dto.request.ContractDetailRequest;
import com.example.contractservice.contract.service.dto.request.ContractReadCursorRequest;
import com.example.contractservice.contract.service.dto.response.MemberInfoResponse.MemberInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContractReadService {

    private static final int PAGE_SIZE = 20;
    private static final int CONTRACT_PARTICIPATION_COUNT = 2;

    private final ContractRepository contractRepository;
    private final MemberClient memberClient;

    public ContractListWithCursorResponse findAllBy(ContractReadCursorRequest request) {
        List<Contract> contracts = contractRepository.findAllBy(request.memberCode(), request.cursor(),
                request.cursorCode(), request.order(), PAGE_SIZE);

        return ContractListWithCursorResponse.of(contracts, PAGE_SIZE);
    }

    public ContractDetailResponse findDetailBy(ContractDetailRequest request) {
        Contract contract = contractRepository.findByCode(request.contractCode());
        ContractInfo contractInfo = contract.getInfo();

        validateMember(request.memberCode(), contract);

        List<MemberInfo> memberInfos = memberClient.getMemberInfo(List.of(contractInfo.clientCode(), contractInfo.freelancerCode())).data()
                .internalMemberInfos();

        if (memberInfos.size() != CONTRACT_PARTICIPATION_COUNT) {
            throw new ContractException(INVALID_MEMBER_COUNT);
        }

        MemberInfo firstMember = memberInfos.get(0);
        MemberInfo secondMember = memberInfos.get(1);

        String clientName = firstMember.memberCode().equals(contractInfo.clientCode()) ? firstMember.nickName() : secondMember.nickName();
        String freelancerCode = secondMember.memberCode().equals(contractInfo.freelancerCode()) ? secondMember.nickName() : firstMember.nickName();

        return ContractDetailResponse.of(contract, clientName, freelancerCode);
    }

    private void validateMember(String memberCode, Contract contract) {
        ContractInfo contractInfo = contract.getInfo();

        if (contractInfo.clientCode().equals(memberCode) || contractInfo.freelancerCode().equals(memberCode)) { // 클라이언트/프리랜서 중 하나에 속한다면 valid
            return;
        }

        throw new ContractException(INVALID_MEMBER);
    }
}
