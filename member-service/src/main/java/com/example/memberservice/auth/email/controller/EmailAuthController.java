package com.example.memberservice.auth.email.controller;

import com.example.memberservice.auth.email.controller.model.dto.CreateEmailAuthRequest;
import com.example.memberservice.auth.email.service.EmailAuthService;
import com.example.memberservice.auth.email.service.model.dto.input.CreateEmailAuthInput;
import com.example.memberservice.auth.email.service.model.dto.input.VerifyEmailAuthInput;
import com.example.memberservice.member.model.enums.MemberRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/auth/email")
@RequiredArgsConstructor
public class EmailAuthController {

    private final EmailAuthService emailAuthService;

    //이메일 인증 전송
    @PostMapping("/{role}")
    public ResponseDto<Empty> sendEmailAuthCode(@PathVariable MemberRole role,
        @RequestHeader(name = "X-CODE") String memberCode,
        @RequestBody CreateEmailAuthRequest request) {

        log.info("memberCode: {}, role: {}, email: {}", memberCode, role, request.email());

        emailAuthService.sendAuthMail(CreateEmailAuthInput.builder()
            .memberCode(memberCode)
            .to(request.email())
            .memberRole(role)
            .build()
        );

        return ResponseDto.success();
    }
    //이메일 인증 확인
    @PostMapping("/{role}/verify")
    public ResponseDto<Empty> verifyEmailAuthCode(@PathVariable MemberRole role,
        @RequestHeader(name = "X-CODE") String memberCode,
        @RequestParam(name = "code") String authCode) {

        log.info("memberCode: {}, role: {}, code: {}", memberCode, role, authCode);

        emailAuthService.verifyAuthCode(
            VerifyEmailAuthInput.builder()
                .authCode(authCode)
                .memberCode(memberCode)
                .memberRole(role)
                .build()
        );

        return ResponseDto.success();
    }
}

