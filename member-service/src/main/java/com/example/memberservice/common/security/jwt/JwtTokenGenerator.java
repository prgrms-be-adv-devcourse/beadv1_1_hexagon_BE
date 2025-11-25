package com.example.memberservice.common.security.jwt;

import io.jsonwebtoken.Jwts;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenGenerator {

    private final JwtKeyProvider jwtKeyProvider;


    private final JwtProperties jwtProperties;

    public String generateAccessToken(String memberCode, boolean isSignedUp) {
        return Jwts.builder()
            .claim(jwtProperties.getMemberCodeClaims(), memberCode)
            .claim(jwtProperties.getIsSignedUpClaims(), isSignedUp)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenTtl()))
            .signWith(jwtKeyProvider.getAccessTokenSignKey())
            .compact();
    }

    public String generateRefreshToken(String memberCode) {
        return Jwts.builder()
            .claim(jwtProperties.getMemberCodeClaims(), memberCode)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshTokenTtl()))
            .signWith(jwtKeyProvider.getRefreshTokenSignKey())
            .compact();
    }
}
