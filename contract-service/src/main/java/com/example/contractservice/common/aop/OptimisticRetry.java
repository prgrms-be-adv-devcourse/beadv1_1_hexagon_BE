package com.example.contractservice.common.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OptimisticRetry {
    @AliasFor("maxAttempts")
    int value() default 3;

    @AliasFor("value")
    int maxAttempts() default 3;

    long delay() default 200; // millisecond

}
