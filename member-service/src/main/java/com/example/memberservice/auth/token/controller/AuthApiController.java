package com.example.memberservice.auth.token.controller;

import com.example.memberservice.common.exception.BusinessException;
import com.example.memberservice.common.exception.ErrorCode;
import com.example.memberservice.common.security.jwt.JwtProperties;
import com.example.memberservice.common.web.CookieGenerator;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import com.example.memberservice.auth.token.controller.swagger.AuthApiControllerSwagger;
import com.example.memberservice.auth.token.service.AuthService;
import com.example.memberservice.auth.token.service.dto.output.TokensOutput;
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
public class AuthApiController implements AuthApiControllerSwagger {

    private final AuthService authService;

    private final JwtProperties jwtProperties;

    @PostMapping("/reissue")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<Empty> reissueAccessTokenByRefreshToken(
        HttpServletResponse httpServletResponse,
        @CookieValue(name = "refresh-token", required = false) String refreshToken) {

        if (refreshToken == null) {
            throw new BusinessException(ErrorCode.UN_AUTHORIZATION);
        }

        TokensOutput output = authService.reissueAccessTokenByRefreshToken(refreshToken);

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

        authService.deleteRefreshTokenToRedis(refreshToken);

        return ResponseDto.success();
    }

}
