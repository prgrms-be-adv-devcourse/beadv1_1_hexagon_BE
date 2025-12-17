package com.example.recommendationservice.client.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.example.recommendationservice.client")
public class FeignClientConfiguration {
}
