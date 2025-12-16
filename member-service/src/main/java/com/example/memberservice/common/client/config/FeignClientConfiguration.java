package com.example.memberservice.common.client.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.example.memberservice.common.client")
public class FeignClientConfiguration {
}
