package com.example.contractservice.contract.service.batch.writer;

import com.example.contractservice.contract.common.ContractStatus;
import com.example.contractservice.contract.domain.Contract;
import com.example.contractservice.contract.repository.ContractRepository;
import org.hexagon.core.events.contract.ContractEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ContractCancelledWriter extends ContractStatusWriter {

    public ContractCancelledWriter(ApplicationEventPublisher applicationEventPublisher,
            ContractRepository contractRepository) {
        super(applicationEventPublisher, contractRepository);
    }

    @Override
    protected void changeStatus(Contract contract) {
        contract.cancel();
    }

    @Override
    protected void publishEvent(Contract contract) {
        applicationEventPublisher.publishEvent(
                new ContractEvent(contract.getInfo().clientCode(), contract.getCode(),
                        contract.getCreatedAt(), ContractStatus.CANCELLED.name()));
    }
}
