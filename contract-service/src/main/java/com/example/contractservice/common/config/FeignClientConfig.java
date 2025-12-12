package com.example.contractservice.common.config;

import com.example.contractservice.ContractServiceApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackageClasses = ContractServiceApplication.class)
public class FeignClientConfig {

}
