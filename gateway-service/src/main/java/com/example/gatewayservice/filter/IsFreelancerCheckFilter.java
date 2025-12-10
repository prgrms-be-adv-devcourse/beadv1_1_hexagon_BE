package com.example.gatewayservice.filter;

import com.example.gatewayservice.common.jwt.JwtTokenParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IsFreelancerFilter {

    public static class Config {

    }

    private final JwtTokenParser jwtTokenParser;



}
