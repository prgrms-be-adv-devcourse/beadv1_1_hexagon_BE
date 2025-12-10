package com.example.gatewayservice.filter;


import com.example.gatewayservice.common.exception.BusinessException;
import com.example.gatewayservice.common.exception.ErrorCode;
import com.example.gatewayservice.common.jwt.JwtTokenParser;
import io.jsonwebtoken.Claims;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IsClientCheckFilter extends
    AbstractGatewayFilterFactory<IsClientCheckFilter.Config> {

    private static final Set<String> ALLOWED_ROLES = Set.of("CLIENT", "BOTH");

    private final JwtTokenParser jwtTokenParser;

    public static class Config {

    }

    public IsClientCheckFilter(JwtTokenParser jwtTokenParser) {
        super(Config.class);
        this.jwtTokenParser = jwtTokenParser;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return(exchange, chain) -> {

            Claims claims = exchange.getAttribute("claims");
            if (claims == null) {
                throw new BusinessException(ErrorCode.UNAUTHORIZATION);
            }

            String role = jwtTokenParser.parseRole(claims)
                .orElseThrow(() -> new BusinessException(ErrorCode.NEED_SIGNUP));

            log.info("role: {}", role);

            if (!ALLOWED_ROLES.contains(role.toUpperCase())) {
                log.info("CLIENT 권한이 없음. role={}", role);
                throw new BusinessException(ErrorCode.FORBIDDEN_CLIENT);
            }

            return chain.filter(exchange);
        };
    }
}
