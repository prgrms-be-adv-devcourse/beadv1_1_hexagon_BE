package com.example.contractservice.contract.service;

import com.example.contractservice.contract.domain.Contract;
import com.example.contractservice.contract.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContractEventService {
    private final ContractRepository contractRepository;
    private final ContractCancelService contractCancelService;

    public void cancelContract(String contractCode) {
        Contract contract = contractRepository.findByCode(contractCode);

        contractCancelService.processCancel(contract);
    }
}
