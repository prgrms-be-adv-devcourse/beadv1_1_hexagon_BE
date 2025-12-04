package com.example.contractservice.common.aop;

import jakarta.persistence.OptimisticLockException;
import java.security.SecureRandom;
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
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final ThreadLocal<Boolean> parentActive = new ThreadLocal<>();

    @Around("@annotation(retryOptions)")
    public Object retry(ProceedingJoinPoint joinPoint, OptimisticRetry retryOptions) throws Throwable {
        if (isAlreadyActivate()) {
            return joinPoint.proceed();
        }

        int retryCount = 0;
        int jitter = retryOptions.jitter();
        int maxAttempts = retryOptions.maxAttempts();
        int delayMillis = retryOptions.delay();
        parentActive.set(true);

        return proceed(joinPoint, retryCount, maxAttempts, delayMillis, jitter);
    }

    private boolean isAlreadyActivate() {
        return parentActive.get() != null && parentActive.get();
    }

    private static Object proceed(ProceedingJoinPoint joinPoint, int retryCount, int maxAttempts, int delayMillis,
            int jitter) throws Throwable {
        Object proceed = null;

        while (retryCount < maxAttempts) {
            try {
                proceed = joinPoint.proceed();

                break;
            } catch (ObjectOptimisticLockingFailureException | OptimisticLockException | StaleObjectStateException e) {
                retryCount++;
                Thread.sleep(delayMillis + RANDOM.nextInt(jitter));
                log.warn("낙관적 락 예외로 인해 재시도 중입니다... 현재 재시도 횟수: {}, 전체 재시도 횟수: {}", retryCount, maxAttempts);

                if (retryCount == maxAttempts) {
                    parentActive.remove();
                    throw new OptimisticLockingFailureException("낙관적 락 예외, 최대 재시도 횟수 초과", e);
                }
            }
        }

        parentActive.remove();
        return proceed;
    }
}
