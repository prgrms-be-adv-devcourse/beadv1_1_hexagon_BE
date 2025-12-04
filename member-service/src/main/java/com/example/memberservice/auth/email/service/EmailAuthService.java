package com.example.memberservice.auth.email.service;

import com.example.memberservice.auth.email.repository.EmailAuthRedisRepository;
import com.example.memberservice.auth.email.util.AuthMailSender;
import com.example.memberservice.common.exception.BusinessException;
import com.example.memberservice.common.exception.ErrorCode;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailAuthService {

    //이메일 전송 객체 만들기
    private final AuthMailSender authMailSender;

    private final EmailAuthRedisRepository emailAuthRedisRepository;

    @Value("${mail.auth-code-expiration-minute}")
    private long mailOffset;

    //이메일 전송
    public void sendFreelancerAuthMail(String memberCode, String to) {
        String code = createCode();

        authMailSender.sendFreelancerAuthCode(to, code);

        emailAuthRedisRepository.setSingleData(buildFreelancerKey(memberCode), code, mailOffset);
    }

    public void sendClientAuthMail(String memberCode, String to) {
        String code = createCode();

        authMailSender.sendClientAuthCode(to, code);

        emailAuthRedisRepository.setSingleData(buildClientKey(memberCode), code, mailOffset);
    }

    //이메일 확인
    public boolean verifyFreelancerAuthCode(String memberCode, String code) {
        Optional<String> OptionalExistAuthCode = emailAuthRedisRepository.getSingleData(
            buildFreelancerKey(memberCode));

        String existAuthCode = OptionalExistAuthCode.orElseThrow(() -> new BusinessException(
            ErrorCode.EMAIL_VERIFICATION_NOT_FOUND));

        if (!code.equals(existAuthCode)) {
            throw new BusinessException(ErrorCode.EMAIL_VERIFICATION_CODE_MISMATCH);
        }

        return true;
    }

    public boolean verifyClientAuthCode(String memberCode, String code) {
        Optional<String> OptionalExistAuthCode = emailAuthRedisRepository.getSingleData(
            buildClientKey(memberCode));

        String existAuthCode = OptionalExistAuthCode.orElseThrow(() -> new BusinessException(
            ErrorCode.EMAIL_VERIFICATION_NOT_FOUND));

        if (!code.equals(existAuthCode)) {
            throw new BusinessException(ErrorCode.EMAIL_VERIFICATION_CODE_MISMATCH);
        }

        return true;
    }

    //private
    private String createCode() {
        int code = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return String.valueOf(code);
    }

    //private
    private String buildFreelancerKey(String memberCode) {
        return "FREELANCER:" + memberCode;
    }

    private String buildClientKey(String memberCode) {
        return "CLIENT:" + memberCode;
    }
}
