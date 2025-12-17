package com.example.contractservice.settlement.common.config;

import com.example.contractservice.deposit.domain.exception.DepositException;
import com.example.contractservice.settlement.domain.Settlement;
import com.example.contractservice.settlement.entity.SettlementEntity;
import com.example.contractservice.settlement.service.batch.processor.SettlementDataProcessor;
import com.example.contractservice.settlement.service.batch.reader.SettlementZeroOffsetItemReader;
import com.example.contractservice.settlement.service.batch.writer.SettlementCustomWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class SettlementStepConfig {
    private static final int RETRY_LIMIT = 3;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SettlementZeroOffsetItemReader settlementZeroOffsetItemReader;
    private final SettlementDataProcessor settlementDataProcessor;
    private final SettlementCustomWriter settlementCustomWriter;

    @Value("${batch.settlement.size}")
    private int batchSize;

    @Bean
    public Step processSettlementStep() {
        return new StepBuilder("processSettlementStep", jobRepository)
                .<SettlementEntity, Settlement>chunk(batchSize, transactionManager)
                .reader(settlementZeroOffsetItemReader)
                .processor(settlementDataProcessor)
                .writer(settlementCustomWriter)
                .faultTolerant()
                .retry(DepositException.class)
                .retry(OptimisticLockingFailureException.class)
                .retryLimit(RETRY_LIMIT)
                .backOffPolicy(backOffPolicy())
                // .listener() // TODO: 재시도 실패 후 리스너 추가. FAILED에 대한 로깅 처리 필요(FAILED는 정산하지 않음)
                .build();
    }

    @Bean
    public BackOffPolicy backOffPolicy() {
        ExponentialBackOffPolicy policy = new ExponentialBackOffPolicy();
        policy.setInitialInterval(2000L);
        policy.setMultiplier(2.0);
        policy.setMaxInterval(10000L);

        return policy;
    }

}
