package com.example.contractservice.common.config;

import com.example.contractservice.ContractServiceApplication;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackageClasses = ContractServiceApplication.class)
public class FeignClientConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer feignEnumCustomizer() {
        return builder ->
                builder.featuresToEnable(
                        DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE
                );
    }
}
