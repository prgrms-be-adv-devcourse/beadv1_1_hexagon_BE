package com.example.contractservice.contract.service.batch.writer;

import com.example.contractservice.contract.domain.Contract;
import com.example.contractservice.contract.repository.ContractRepository;
import com.example.contractservice.contract.service.mapper.ContractMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.ApplicationEventPublisher;

@StepScope
@RequiredArgsConstructor
public abstract class ContractStatusWriter implements ItemWriter<Contract> {

    protected final ApplicationEventPublisher applicationEventPublisher;
    protected final ContractRepository contractRepository;

    @Override
    public void write(Chunk<? extends Contract> chunk) {
        chunk.forEach(contract -> {
            changeStatus(contract);

            contractRepository.saveContract(contract);

            publishEvent(contract);
        });
    }

    protected void publishEvent(Contract contract) {
        applicationEventPublisher.publishEvent(ContractMapper.toContractEvent(contract));
    }

    protected abstract void changeStatus(Contract contract);
}
