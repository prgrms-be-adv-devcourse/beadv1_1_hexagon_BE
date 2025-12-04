package com.example.memberservice.auth.email.service;

import com.example.memberservice.auth.email.repository.EmailAuthRedisRepository;
import com.example.memberservice.auth.email.service.model.dto.input.CreateEmailAuthInput;
import com.example.memberservice.auth.email.util.AuthMailSender;
import com.example.memberservice.common.exception.BusinessException;
import com.example.memberservice.common.exception.ErrorCode;
import com.example.memberservice.member.model.enums.MemberRole;
import com.example.memberservice.member.service.MemberService;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailAuthService {

    //이메일 전송 객체 만들기
    private final AuthMailSender authMailSender;

    private final EmailAuthRedisRepository emailAuthRedisRepository;

    private final MemberService memberService;

    //이메일 전송
    public void sendAuthMail(CreateEmailAuthInput createEmailAuthInput) {
        String authCode = createAuthCode();

        MemberRole memberRole = createEmailAuthInput.memberRole();
        String memberCode = createEmailAuthInput.memberCode();
        String to = createEmailAuthInput.to();

        authMailSender.sendAuthCode(memberRole, to, authCode);

        emailAuthRedisRepository.saveAuthCode(memberRole, memberCode, authCode);
    }

    //이메일 확인
    public boolean verifyAuthCode(MemberRole memberRole, String memberCode, String code) {
        Optional<String> optionalExistAuthCode = emailAuthRedisRepository.findAuthCodeByMemberCode(
            memberRole, memberCode);

        String existAuthCode = optionalExistAuthCode.orElseThrow(
            () -> new BusinessException(ErrorCode.EMAIL_VERIFICATION_NOT_FOUND));

        if (!code.equals(existAuthCode)) {
            throw new BusinessException(ErrorCode.EMAIL_VERIFICATION_CODE_MISMATCH);
        }

        return true;
    }

    //private
    private String createAuthCode() {
        int code = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return String.valueOf(code);
    }


}
