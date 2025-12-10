package com.example.memberservice.common.security.jwt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JwtProperties {

    @Value("${jwt.access-token.ttl}")
    private long accessTokenTtl;

    @Value("${jwt.refresh-token.ttl}")
    private long refreshTokenTtl;

    @Value("${jwt.claims.member-code}")
    private String memberCodeClaims;

    @Value("${jwt.claims.is-sign}")
    private String isSignedUpClaims;

    @Value("${jwt.claims.member-code}")
    private String memberRoleClaims;
}
