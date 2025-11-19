package com.example.contractservice.contract.service.batch.writer;

import com.example.contractservice.contract.common.ContractStatus;
import com.example.contractservice.contract.entity.ContractEntity;
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
    protected void changeStatus(ContractEntity contractEntity) {
        contractEntity.updateStatus(ContractStatus.DONE);
    }

    @Override
    protected void publishEvent(ContractEntity contractEntity) {
        applicationEventPublisher.publishEvent(new ContractEvent(contractEntity.getCode(), contractEntity.getCreatedAt(), ContractStatus.DONE.name()));
    }
}
