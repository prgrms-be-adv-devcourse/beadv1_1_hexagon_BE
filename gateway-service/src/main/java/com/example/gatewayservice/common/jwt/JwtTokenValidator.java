package com.example.gatewayservice.common.jwt;

import com.example.gatewayservice.common.exception.BusinessException;
import com.example.gatewayservice.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenValidator {

    private final JwtKeyProvider jwtKeyProvider;

    //AccessToken의 검증은 Api Gateway에서 일어나기 떄문에 Member 모듈에서는 AccessToken 검증은 생략
    public Claims validateAccessToken(String token) {
        try {
            return Jwts.parser()
                .verifyWith(jwtKeyProvider.getAccessTokenSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        }catch(ExpiredJwtException e){
            log.info("accessToken이 만료되었습니다.");
            throw new BusinessException(ErrorCode.NEED_RE_ISSUE);
        }
        catch (JwtException e) {
            log.info("acesssToken 검증이 실패했습니다.");
            throw new BusinessException(ErrorCode.UNAUTHORIZATION);
        }
    }
}
