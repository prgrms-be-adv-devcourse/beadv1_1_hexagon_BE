package com.example.memberservice.common.security.jwt;


import io.jsonwebtoken.Claims;
import com.example.memberservice.common.exception.BusinessException;
import com.example.memberservice.common.exception.ErrorCode;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenValidator {

    private final JwtKeyProvider jwtKeyProvider;

    //AccessToken의 검증은 Api Gateway에서 일어나기 떄문에 Member 모듈에서는 AccessToken 검증은 생략
    public Claims validateRefreshToken(String token) {
        try {
            return Jwts.parser()
                .verifyWith(jwtKeyProvider.getRefreshTokenSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (JwtException e) {
            //TODO JwtToken 관련 비지니스 Exception으로 변경
            throw new BusinessException(ErrorCode.UN_AUTHORIZATION);

        }
    }
}
