package com.example.contractservice.common.aop;

import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.StaleObjectStateException;
import org.springframework.core.annotation.Order;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@Order(10) // 따로 지정하지 않으면 최하위 순서가 되며 이는 @Transactional과 같다. 허나 같으면 @Transactional이 먼저 적용된다. 왜일까
public class OptimisticRetryAspect {

    @Around("@annotation(retryOptions)")
    public Object retry(ProceedingJoinPoint joinPoint, OptimisticRetry retryOptions) throws Throwable {
        log.info("시도 시작!");
        int retryCount = 0;
        int maxAttempts = retryOptions.maxAttempts();
        long delayMillis = retryOptions.delay();

        while (retryCount < maxAttempts) {
            try {
                return joinPoint.proceed();
            } catch (ObjectOptimisticLockingFailureException | OptimisticLockException | StaleObjectStateException e) { // 낙관적 락 예외만 재시도한다.
                retryCount++;
                log.warn("낙관적 락 예외로 인해 재시도 중입니다... 현재 재시도 횟수: {}, 전체 재시도 횟수: {}", retryCount, maxAttempts);
            } // 그 외 예외는 재시도 없이 바로 던져진다.

            Thread.sleep(delayMillis);
        }
        throw new OptimisticLockingFailureException("낙관적 락 예외, 최대 재시도 횟수 초과");
    }
}
