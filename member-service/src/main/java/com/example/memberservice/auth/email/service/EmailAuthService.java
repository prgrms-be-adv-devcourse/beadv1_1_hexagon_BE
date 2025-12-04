package com.example.memberservice.auth.email.service;

import com.example.memberservice.auth.email.repository.EmailAuthRedisService;
import com.example.memberservice.auth.email.util.AuthMailSender;
import com.example.memberservice.common.exception.BusinessException;
import com.example.memberservice.common.exception.ErrorCode;
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

    @Value("${mail.auth-code-expiration-minute}")
    private long mailOffset;

    //이메일 전송
    public void sendAuthMailToFreelancer(String memberCode, String to) {
        String code = createCode();

        authMailSender.sendFreelancerAuthCode(to, code);

        emailAuthRedisService.createFreelancerAuthCode(buildFreelancerKey(memberCode), code, mailOffset);
    }

    public void sendAuthMailToClient(String memberCode, String to) {
        String code = createCode();

        authMailSender.sendClientAuthCode(to, code);

        emailAuthRedisService.createClientAuthCode(buildClientKey(memberCode), code, mailOffset);
    }

    //이메일 확인
    public boolean verifyFreelancerAuthCode(String memberCode, String code) {
        String existAuthCode = emailAuthRedisService.findFreelancerAuthCodeByMemberCode(buildFreelancerKey(memberCode));

        if (!code.equals(existAuthCode)) {
            throw new BusinessException(ErrorCode.EMAIL_VERIFICATION_CODE_MISMATCH);
        }


        return true;
    }

    public boolean verifyClientAuthCode(String memberCode, String code) {
        Optional<String> OptionalExistAuthCode = emailAuthRedisService.findClientAuthCodeByMemberCode(
            buildClientKey(memberCode));

        String existAuthCode = OptionalExistAuthCode.orElseThrow(() -> new BusinessException(
            ErrorCode.EMAIL_VERIFICATION_NOT_FOUND));

        if (!code.equals(existAuthCode)) {
            throw new BusinessException(ErrorCode.EMAIL_VERIFICATION_CODE_MISMATCH);
        }

        //이 후 멤버 롤이 추가 되면 이것을 호출
        memberService.updateMemberWorkState(new MemberUpdateWorkStateInput(memberCode));

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
