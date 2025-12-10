package com.example.gatewayservice.filter;

import com.example.gatewayservice.common.exception.BusinessException;
import com.example.gatewayservice.common.exception.ErrorCode;
import com.example.gatewayservice.common.jwt.JwtTokenParser;
import com.example.gatewayservice.filter.IsFreelancerCheckFilter.Config;
import io.jsonwebtoken.Claims;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IsFreelancerCheckFilter extends AbstractGatewayFilterFactory<Config> {

    private final JwtTokenParser jwtTokenParser;

    private static final Set<String> ALLOWED_ROLES = Set.of("FREELANCER", "BOTH");

    public IsFreelancerCheckFilter(JwtTokenParser jwtTokenParser) {
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
                throw new BusinessException(ErrorCode.UNAUTHORIZATION);
            }

            String role = jwtTokenParser.parseRole(claims)
                .orElseThrow(() -> new BusinessException(ErrorCode.NEED_SIGNUP));

            log.info("role: {}", role);

            if (!ALLOWED_ROLES.contains(role.toUpperCase())) {
                log.info("FREELANCER 권한이 없음. role={}", role);
                throw new BusinessException(ErrorCode.FORBIDDEN_FREELANCER);
            }

            return chain.filter(exchange);
        };
    }
}