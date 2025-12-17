package com.example.gatewayservice.common.jwt;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JwtProperties {

    @Value("${jwt.claims.member-code}")
    private String memberCodeClaims;

    @Value("${jwt.claims.is-sign}")
    private String isSignClaims;

    @Value("${jwt.claims.member-role}")
    private String memberRoleClaims;
}
