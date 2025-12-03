package com.example.memberservice.oauth.controller;

import com.example.memberservice.common.exception.BusinessException;
import com.example.memberservice.common.exception.ErrorCode;
import com.example.memberservice.common.security.jwt.JwtProperties;
import com.example.memberservice.common.web.CookieGenerator;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import com.example.memberservice.oauth.controller.swagger.OAuthApiControllerSwagger;
import com.example.memberservice.oauth.service.OAuthService;
import com.example.memberservice.oauth.service.dto.output.TokensOutput;
import jakarta.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OAuthApiController implements OAuthApiControllerSwagger {

    private final OAuthService oAuthService;

    private final JwtProperties jwtProperties;

    @PostMapping("/reissue")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<Empty> reissueAccessTokenByRefreshToken(
        HttpServletResponse httpServletResponse,
        @CookieValue(name = "refresh-token", required = false) String refreshToken) {

        if (refreshToken == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZATION);
        }

        TokensOutput output = oAuthService.reissueAccessTokenByRefreshToken(refreshToken);

        httpServletResponse.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + output.accessToken());
        httpServletResponse.setHeader(HttpHeaders.SET_COOKIE,
            CookieGenerator.createCookies("refresh-token", output.refreshToken(),
                TimeUnit.MILLISECONDS.toSeconds(jwtProperties.getRefreshTokenTtl())));

        return ResponseDto.success();
    }


    @DeleteMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<Empty> logoutMemberByRefreshToken(
        @CookieValue("refresh-token") String refreshToken) {

        oAuthService.deleteRefreshTokenToRedis(refreshToken);

        return ResponseDto.success();
    }

}
