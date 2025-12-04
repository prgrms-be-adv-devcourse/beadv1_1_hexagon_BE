package com.example.memberservice.auth.email.util;

import com.example.memberservice.common.mail.CustomMailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthMailSender {

    private final CustomMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendFreelancerAuthCode(String to, String code) {

        String subject = "[이어드림] Freelancer 이메일 인증 진행 메일입니다.";
        StringBuilder body = new StringBuilder();
        body.append("<h3>Freelancer 이메일 인증 번호입니다.</h3>")
            .append("<h1>").append(code).append("</h1>")
            .append("<h3>감사합니다.</h3>");

        mailSender.send(from, to, subject, body.toString());
    }

    public void sendClientAuthCode(String to, String code) {
        String subject = "[이어드림] Client 이메일 인증 진행 메일입니다.";
        StringBuilder body = new StringBuilder();
        body.append("<h3>Client 이메일 인증 번호입니다.</h3>")
            .append("<h1>").append(code).append("</h1>")
            .append("<h3>감사합니다.</h3>");

        mailSender.send(from, to, subject, body.toString());
    }

}
