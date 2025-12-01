package com.example.contractservice.contract.service;

import com.example.contractservice.contract.common.ContractStatus;
import com.example.contractservice.contract.domain.exception.ContractErrorCode;
import com.example.contractservice.contract.domain.exception.ContractException;
import com.example.contractservice.contract.entity.ContractEntity;
import com.example.contractservice.contract.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContractEventService {
    private final ContractRepository contractRepository;

    public void cancelContract(String code) {
        ContractEntity contractEntity = contractRepository.findByCode(code);

        if (contractEntity.getStatus() != ContractStatus.REQUESTED) {
            throw new ContractException(ContractErrorCode.NOT_REQUESTED_STATUS);
        }

        contractEntity.updateStatus(ContractStatus.CANCELLED);

        contractRepository.saveContract(contractEntity);
    }
}
