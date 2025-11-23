package com.example.gatewayservice.filter;

import com.example.gatewayservice.common.exception.BusinessException;
import com.example.gatewayservice.common.exception.ErrorCode;
import com.example.gatewayservice.common.jwt.JwtTokenParser;
import com.example.gatewayservice.filter.IsSignCheckFilter.Config;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class IsSignCheckFilter extends AbstractGatewayFilterFactory<Config> {

    private final JwtTokenParser jwtTokenParser;

    public IsSignCheckFilter(JwtTokenParser jwtTokenParser) {
        super(Config.class);
        this.jwtTokenParser = jwtTokenParser;
    }

    public static class Config {

    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {
            Claims claims = exchange.getAttribute("claims");

            if (claims == null) {
                log.info("이전 필터에서 claims 값을 받지 못했습니다.");
                throw new BusinessException(ErrorCode.UNAUTHORIZATION);
            }

            Boolean isSign = jwtTokenParser.parseIsSign(claims);

            if (!Boolean.TRUE.equals(isSign)) {
                log.info("is-signed-up의 값이 fasle 입니다. 회원가입이 필요한 멤버입니다.");
                throw new BusinessException(ErrorCode.NEED_SIGNUP);
            }

            return chain.filter(exchange);
        };
    }


}
