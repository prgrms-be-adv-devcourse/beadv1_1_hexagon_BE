package com.example.memberservice.common.mail;

import com.example.memberservice.common.exception.BusinessException;
import com.example.memberservice.common.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomMailSender {

    private final JavaMailSender mailSender;

//    @Async
    public void send(String from, String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            message.setFrom(from);
            message.setRecipients(MimeMessage.RecipientType.TO, to);
            message.setSubject(subject);
            message.setText(body, "UTF-8", "html");

            mailSender.send(message);
        } catch (MailException e) {
            // 실패 로그 출력
            log.error("메일 전송 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.MAIL_SEND_FAILED);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}