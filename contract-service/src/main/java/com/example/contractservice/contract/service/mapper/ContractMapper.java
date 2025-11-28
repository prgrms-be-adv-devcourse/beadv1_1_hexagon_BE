package com.example.contractservice.contract.service.mapper;

import com.example.contractservice.contract.domain.Contract;
import com.example.contractservice.contract.domain.vo.ContractContent;
import com.example.contractservice.contract.domain.vo.ContractInfo;
import com.example.contractservice.contract.entity.ContractEntity;

public abstract class ContractMapper {

    private ContractMapper() {}

    public static ContractEntity toEntity(Contract contract) {
        ContractInfo contractInfo = contract.getInfo();
        ContractContent contractContent = contract.getContent();

        return ContractEntity.builder()
                .code(contract.getCode())
                .clientCode(contractInfo.clientCode())
                .freelancerCode(contractInfo.freelancerCode())
                .status(contractInfo.status())
                .startedAt(contractInfo.startedAt())
                .endedAt(contractInfo.endedAt())
                .unitAmount(contractInfo.unitAmount())
                .paymentType(contractInfo.paymentType())
                .name(contractContent.name())
                .body(contractContent.body())
                .build();
    }

    public static Contract toDomain(ContractEntity contractEntity) {
        ContractInfo contractInfo = new ContractInfo(
                contractEntity.getClientCode(),
                contractEntity.getFreelancerCode(),
                contractEntity.getFreelancerCode(),
                contractEntity.getStartedAt(),
                contractEntity.getEndedAt(),
                contractEntity.getPaymentType(),
                contractEntity.getUnitAmount(),
                contractEntity.getStatus());

        ContractContent contractContent = new ContractContent(contractEntity.getName(), contractEntity.getBody());

        return new Contract(contractEntity.getCode(), contractInfo, contractContent, contractEntity.getCreatedAt(),
                contractEntity.getUpdatedAt());
    }

    public static void applyToEntity(Contract contract, ContractEntity contractEntity) {
        ContractInfo contractInfo = contract.getInfo();
        ContractContent contractContent = contract.getContent();

        contractEntity.updateInfo(
                contractInfo.startedAt(),
                contractInfo.endedAt(),
                contractInfo.paymentType(),
                contractInfo.unitAmount(),
                contractInfo.status(),
                contractContent.name(),
                contractContent.body()
        );
    }
}
