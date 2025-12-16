package com.example.contractservice.contract.service.batch.writer;

import com.example.contractservice.contract.common.ContractStatus;
import com.example.contractservice.contract.domain.Contract;
import com.example.contractservice.contract.repository.ContractRepository;
import org.hexagon.core.events.contract.ContractEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ContractDoneWriter extends ContractStatusWriter {

    public ContractDoneWriter(ApplicationEventPublisher applicationEventPublisher,
            ContractRepository contractRepository) {
        super(applicationEventPublisher, contractRepository);
    }

    @Override
    protected void changeStatus(Contract contractEntity) {
        contractEntity.done();
    }

    @Override
    protected void publishEvent(Contract contract) {
        applicationEventPublisher.publishEvent(
                new ContractEvent(contract.getCode(), contract.getInfo().commissionCode(),
                        contract.getCreatedAt(), ContractStatus.DONE.name()));
    }
}
