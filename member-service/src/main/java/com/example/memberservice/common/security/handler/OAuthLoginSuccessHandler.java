package com.example.memberservice.common.security.handler;


import com.example.memberservice.common.exception.BusinessException;
import com.example.memberservice.common.redis.service.RedisSingleDataService;
import com.example.memberservice.common.security.jwt.JwtProperties;
import com.example.memberservice.common.security.jwt.JwtTokenGenerator;
import com.example.memberservice.common.security.model.dto.CustomOAuth2UserDto;
import com.example.memberservice.common.web.CookieGenerator;
import com.example.memberservice.member.repository.MemberJpaRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuthLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RedisSingleDataService redisSingleDataService;

    private final JwtTokenGenerator tokenGenerator;

    private final MemberJpaRepository memberJpaRepository;

    private final OAuthLoginFailureHandler oAuthLoginFailureHandler;


    private final JwtProperties jwtProperties;

    @Value("${redirect-url.login.success}")
    private String successRedirectUrl;

    @Value("${redirect-url.login.need-signup}")
    private String needSignUpRedirectUrl;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2UserDto oAuth2User = (CustomOAuth2UserDto) authentication.getPrincipal();

        String memberCode = oAuth2User.getMemberCode();

        String refreshToken = tokenGenerator.generateRefreshToken(memberCode);

        //레디스에  prefix:member code - refreshToken 형태로 저장. ttl 은 14일

        log.info("TOKEN:%s".formatted(memberCode));
        try {
            redisSingleDataService.setSingleData(memberCode, refreshToken, jwtProperties.getRefreshTokenTtl());

        } catch (BusinessException e) {
            oAuthLoginFailureHandler.onAuthenticationFailure(request, response,
                new AuthenticationServiceException(e.getErrorCode().getMessage(), e));

            return;
        }

        String redirectUri;

        //소셜로그인에 회원가입까지 완료했다면
        if (memberJpaRepository.existsByCode(memberCode)) {

            redirectUri = successRedirectUrl;
        } else {
            redirectUri = needSignUpRedirectUrl;
        }

        response.addHeader(HttpHeaders.SET_COOKIE, CookieGenerator.createCookies("refresh-token", refreshToken,
            TimeUnit.MILLISECONDS.toSeconds(jwtProperties.getRefreshTokenTtl())));

        response.sendRedirect(redirectUri);
    }
}
