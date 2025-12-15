package com.example.contractservice.common.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

/**
 * 낙관적 락에 의한 재시도를 위해 만들어진 어노테이션. <br />
 * 각각은 다음 의미를 지닙니다. <br />
 * - maxAttempts: 최대 재시도 횟수 <br />
 * - delay: 재시도 간 최소 간격 <br />
 * - jitter: delay에 추가되는 최대 지연 시간 <br />
 * <br />
 * 만약에 delay가 200이고 jitter가 100이면 (200 + @)ms 의 시간동안 지연된 뒤, 재시도합니다. 이때 @는 jitter 값을 넘지 않는 음이 아닌 랜덤 정수입니다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OptimisticRetry {
    @AliasFor("maxAttempts")
    int value() default 3;

    @AliasFor("value")
    int maxAttempts() default 3;

    int delay() default 200; // millisecond

    int jitter() default 200; // millisecond

}
