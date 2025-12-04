package com.example.memberservice.auth.email.service;

import com.example.memberservice.auth.email.repository.EmailAuthRedisService;
import com.example.memberservice.auth.email.util.AuthMailSender;
import com.example.memberservice.common.exception.BusinessException;
import com.example.memberservice.common.exception.ErrorCode;
import com.example.memberservice.member.model.enums.MemberRole;
import com.example.memberservice.member.service.MemberService;
import com.example.memberservice.member.service.model.dto.input.MemberUpdateWorkStateInput;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailAuthService {

    //이메일 전송 객체 만들기
    private final AuthMailSender authMailSender;

    private final EmailAuthRedisService emailAuthRedisService;

    private final MemberService memberService;

    //이메일 전송
    public void sendAuthMailToFreelancer(String memberCode, String to) {
        String authCode = createAuthCode();

        authMailSender.sendFreelancerAuthCode(to, authCode);

        emailAuthRedisService.createAuthCode(MemberRole.FREELANCER, memberCode, authCode);
    }

    public void sendAuthMailToClient(String memberCode, String to) {
        String authCode = createAuthCode();

        authMailSender.sendClientAuthCode(to, authCode);

        emailAuthRedisService.createAuthCode(MemberRole.CLIENT, memberCode, authCode);
    }

    //이메일 확인
    public boolean verifyFreelancerAuthCode(String memberCode, String code) {
        Optional<String> optionalExistAuthCode = emailAuthRedisService.findAuthCodeByMemberCode(
            MemberRole.FREELANCER, memberCode);

        String existAuthCode = optionalExistAuthCode.orElseThrow(
            () -> new BusinessException(ErrorCode.EMAIL_VERIFICATION_NOT_FOUND));

        if (!code.equals(existAuthCode)) {
            throw new BusinessException(ErrorCode.EMAIL_VERIFICATION_CODE_MISMATCH);
        }

        return true;
    }

    public boolean verifyClientAuthCode(String memberCode, String code) {
        Optional<String> optionalExistAuthCode = emailAuthRedisService.findAuthCodeByMemberCode(
            MemberRole.CLIENT, memberCode);

        String existAuthCode = optionalExistAuthCode.orElseThrow(() -> new BusinessException(
            ErrorCode.EMAIL_VERIFICATION_NOT_FOUND));

        if (!code.equals(existAuthCode)) {
            throw new BusinessException(ErrorCode.EMAIL_VERIFICATION_CODE_MISMATCH);
        }

        emailAuthRedisService.createAuthVerification(MemberRole.CLIENT, memberCode);

        return true;
    }

    //private
    private String createAuthCode() {
        int code = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return String.valueOf(code);
    }


}
