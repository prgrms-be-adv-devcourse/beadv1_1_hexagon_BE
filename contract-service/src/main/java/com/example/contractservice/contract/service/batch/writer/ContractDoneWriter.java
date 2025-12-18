package com.example.contractservice.contract.service.batch.writer;

import com.example.contractservice.contract.domain.Contract;
import com.example.contractservice.contract.repository.ContractRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ContractDoneWriter extends ContractStatusWriter {

    public ContractDoneWriter(ApplicationEventPublisher applicationEventPublisher,
            ContractRepository contractRepository) {
        super(applicationEventPublisher, contractRepository);
    }

    @Override
    protected void changeStatus(Contract contract) {
        contract.done();
    }
}
