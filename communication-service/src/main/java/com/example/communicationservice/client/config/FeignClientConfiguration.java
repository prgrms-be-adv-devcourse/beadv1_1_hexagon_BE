package com.example.communicationservice.client.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.example.communicationservice.client")
public class FeignClientConfiguration {
}
