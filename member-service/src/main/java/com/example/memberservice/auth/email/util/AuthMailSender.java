package com.example.memberservice.auth.email.util;

import com.example.memberservice.common.mail.CustomMailSender;
import com.example.memberservice.member.model.enums.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthMailSender {

    private final CustomMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendAuthCode(MemberRole memberRole, String to, String code) {

        String subject = "[이어드림] %s 이메일 인증 진행 메일입니다.".formatted(memberRole.toString());
        StringBuilder body = new StringBuilder();
        body.append("<h3>%s 이메일 인증 번호입니다.</h3>".formatted(memberRole.toString()))
            .append("<h1>").append(code).append("</h1>")
            .append("<h3>감사합니다.</h3>");

        mailSender.send(from, to, subject, body.toString());
    }

}
