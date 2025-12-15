package com.example.contractservice.contract.common.config;

import com.example.contractservice.contract.domain.Contract;
import com.example.contractservice.contract.entity.ContractEntity;
import com.example.contractservice.contract.service.batch.writer.ContractStatusWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ContractStepConfig { // TODO: 실패, 에러 시 리스너 추가 + ContractEntity 제거
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final ItemReader<ContractEntity> contractInProgressReader;
    private final ItemReader<ContractEntity> contractPaidReader;
    private final ItemReader<ContractEntity> contractRequestedReader;

    private final ContractStatusWriter contractDoneWriter;
    private final ContractStatusWriter contractInProgressWriter;
    private final ContractStatusWriter contractCancelledWriter;

    @Value("${batch.contract.size}")
    private int chunkSize;

    @Bean
    public Step contractToDoneBatchStep() { // IN_PROGRESS -> DONE
        return new StepBuilder("contractToDoneBatchStep", jobRepository)
                .<ContractEntity, Contract>chunk(chunkSize, transactionManager)
                .reader(contractInProgressReader)
                .writer(contractDoneWriter)
                .build();
    }

    @Bean
    public Step contractToInProgressBatchStep() { // PAID -> IN_PROGRESS
        return new StepBuilder("contractToInProgressBatchStep", jobRepository)
                .<ContractEntity, Contract>chunk(chunkSize, transactionManager)
                .reader(contractPaidReader)
                .writer(contractInProgressWriter)
                .build();
    }

    @Bean
    public Step contractToCancelledBatchStep() { // REQUESTED -> CANCELLED
        return new StepBuilder("contractToCancelledBatchStep", jobRepository)
                .<ContractEntity, Contract>chunk(chunkSize, transactionManager)
                .reader(contractRequestedReader)
                .writer(contractCancelledWriter)
                .build();
    }
}
