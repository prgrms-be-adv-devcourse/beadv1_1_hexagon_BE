package com.example.gatewayservice.common.jwt;

import com.example.gatewayservice.common.exception.BusinessException;
import com.example.gatewayservice.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenParser {

    private final JwtProperties jwtProperties;

    public String parseMemberCode(Claims claims) {
        Object memberCode = claims.get(jwtProperties.getMemberCodeClaims());
        if (memberCode == null) {
            log.info("claims에 member code가 포함되어 있지 않습니다.;");
            throw new BusinessException(ErrorCode.UNAUTHORIZATION);
        }
        return memberCode.toString();
    }

    public boolean parseIsSign(Claims claims) {
        boolean isSign;
        try{
            String isSignStr = claims.get(jwtProperties.getIsSignClaims()).toString();
            isSign = Boolean.parseBoolean(isSignStr);
        }catch (Exception e){
            log.info("claims에 is-signed-up 이 존재하지 않습니다.");
            throw new BusinessException(ErrorCode.UNAUTHORIZATION);
        }

        return isSign;
    }
}
