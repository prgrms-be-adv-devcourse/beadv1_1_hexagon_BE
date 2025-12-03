package com.example.memberservice.auth.email.controller;

import com.example.memberservice.auth.email.service.EmailAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
public class EmailAuthController {

    private final EmailAuthService emailAuthService;

    //이메일 인증 전송

    //이메일 인증 확인
}

