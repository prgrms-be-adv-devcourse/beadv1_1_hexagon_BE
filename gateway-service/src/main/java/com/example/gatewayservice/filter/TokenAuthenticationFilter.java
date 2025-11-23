package com.example.gatewayservice.filter;

import com.example.gatewayservice.common.exception.BusinessException;
import com.example.gatewayservice.common.exception.ErrorCode;
import com.example.gatewayservice.common.jwt.JwtTokenParser;
import com.example.gatewayservice.common.jwt.JwtTokenValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
@Slf4j
public class TokenAuthenticationFilter extends AbstractGatewayFilterFactory<TokenAuthenticationFilter.Config> {


    public static class Config {

    }

    private final JwtTokenValidator jwtTokenValidator;

    private final JwtTokenParser jwtTokenParser;

    public TokenAuthenticationFilter(ObjectMapper om, JwtTokenValidator jwtTokenValidator,
        JwtTokenParser jwtTokenParser) {
        super(Config.class);
        this.jwtTokenValidator = jwtTokenValidator;
        this.jwtTokenParser = jwtTokenParser;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            log.info("토큰 검증 로직 실행");
            ServerHttpRequest request = exchange.getRequest();
            // Request에서 토큰 및 헤더가 있는 지 확인
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                log.info("AccessToken이 비어있습니다. RefreshToken을 확인합니다.");

                if (!request.getCookies().containsKey("refresh-token")) {
                    log.error("사용자 인증 정보가 없습니다. 재로그인을 진행해주세요");

                    //재로그인을 하라는 응답을 보내고 싶음
                    throw new BusinessException(ErrorCode.NEED_RE_LOGIN);
                } else {
                    log.info("RefreshToken을 통해 AccessToken 재요청 로직을 수행하세요.");

                    // AccessToken 재발급 하라는 응답을 보내고 싶음.
                    throw new BusinessException(ErrorCode.NEED_RE_ISSUE);
                }
            }

            String accessToken = resolveToken(request).orElseThrow(
                () -> new BusinessException(ErrorCode.NEED_RE_ISSUE));

            Claims claims = jwtTokenValidator.validateAccessToken(accessToken);

            String memberCode = jwtTokenParser.parseMemberCode(claims);

            ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-CODE", memberCode)
                .build();

            //다음 IsSignCheckFilter에서 확인할 수도 있으니 claims를 넘겨줌
            ServerWebExchange mutatedExchange = exchange.mutate()
                .request(mutatedRequest)
                .build();

            // mutate로 새 exchange 만들었으니 새 exchange에 다시 넣어야 한다
            mutatedExchange.getAttributes().put("claims", claims);

            return chain.filter(mutatedExchange);
        };
    }

    private Optional<String> resolveToken(ServerHttpRequest request) {

        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return Optional.of(bearerToken.substring(7));
        }

        HttpCookie tokenCookie = request.getCookies().getFirst("token");
        if (tokenCookie != null) {
            return Optional.of(tokenCookie.getValue());
        }

        return Optional.empty();
    }
}
