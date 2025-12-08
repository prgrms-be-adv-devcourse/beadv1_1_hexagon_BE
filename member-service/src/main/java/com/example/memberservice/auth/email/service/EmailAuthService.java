package com.example.memberservice.auth.email.service;

import com.example.memberservice.auth.email.repository.EmailAuthRedisRepository;
import com.example.memberservice.auth.email.service.model.dto.input.CreateEmailAuthInput;
import com.example.memberservice.auth.email.service.model.dto.input.VerifyEmailAuthInput;
import com.example.memberservice.auth.email.util.AuthMailSender;
import com.example.memberservice.common.exception.BusinessException;
import com.example.memberservice.common.exception.ErrorCode;
import com.example.memberservice.member.model.enums.MemberRole;
import com.example.memberservice.member.repository.MemberJpaRepository;
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

    private final MemberJpaRepository memberJpaRepository;

    //이메일 전송
    public void sendAuthMail(CreateEmailAuthInput createEmailAuthInput) {

        MemberRole memberRole = createEmailAuthInput.memberRole();
        String memberCode = createEmailAuthInput.memberCode();
        String to = createEmailAuthInput.to();

        memberJpaRepository.findByCode(memberCode)
            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        String authCode = createAuthCode();

        authMailSender.sendAuthCode(memberRole, to, authCode);

        emailAuthRedisRepository.deleteRetryCount(memberRole, memberCode);

        emailAuthRedisRepository.saveAuthCode(memberRole, memberCode, authCode);
    }

    //이메일 확인
    public boolean verifyAuthCode(VerifyEmailAuthInput verifyEmailAuthInput) {

        String memberCode = verifyEmailAuthInput.memberCode();

        MemberRole memberRole = verifyEmailAuthInput.memberRole();

        String authCode = verifyEmailAuthInput.authCode();

        Long retryCount = emailAuthRedisRepository.incrementRetryCount(memberRole, memberCode);

        if (retryCount > 5) {
            throw new BusinessException(ErrorCode.EMAIL_VERIFICATION_EXCEEDED);
        }

        memberJpaRepository.findByCode(verifyEmailAuthInput.memberCode())
            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Optional<String> optionalExistAuthCode = emailAuthRedisRepository.findAuthCodeByMemberCode(
            verifyEmailAuthInput.memberRole(), verifyEmailAuthInput.memberCode());

        String existAuthCode = optionalExistAuthCode.orElseThrow(
            () -> new BusinessException(ErrorCode.EMAIL_VERIFICATION_NOT_FOUND));

        if (!authCode.equals(existAuthCode)) {
            throw new BusinessException(ErrorCode.EMAIL_VERIFICATION_CODE_MISMATCH);
        }

        //이메일 검증 완료 데이터 추가 이후 최종 상태 변경전 해당 값 확인
        emailAuthRedisRepository.saveAuthVerification(memberRole, memberCode);

        //이메일 검증 데이터 삭제
        emailAuthRedisRepository.deleteAuthCode(memberRole, memberCode);

        return true;
    }

    //private
    private String createAuthCode() {
        int code = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return String.valueOf(code);
    }


}
