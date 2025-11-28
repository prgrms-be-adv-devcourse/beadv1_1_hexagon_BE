package com.example.memberservice.common.security.handler;


import static com.example.memberservice.common.web.ResponseDtoMapper.getErrorResponse;

import com.example.memberservice.common.exception.ErrorCode;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {


    @Value("${redirect-url.login.failure}")
    private String failureRedirectUrl;

    private final ObjectMapper om;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException exception) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");


        ResponseDto<Empty> responseBody = getErrorResponse(ErrorCode.FAIL_LOGIN);

        response.sendRedirect(failureRedirectUrl);

        response.getWriter().write(om.writeValueAsString(responseBody));
    }
}

