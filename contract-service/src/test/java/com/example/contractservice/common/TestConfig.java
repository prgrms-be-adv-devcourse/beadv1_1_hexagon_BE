package com.example.contractservice.common;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public ApplicationEventPublisher testEventPublisher() {
        return event -> System.out.println(event + " 발행");
    }
}
