package com.example.memberservice.auth.email.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.example.memberservice.auth.email.repository.EmailAuthRedisRepository;
import com.example.memberservice.auth.email.service.model.dto.input.CreateEmailAuthInput;
import com.example.memberservice.auth.email.service.model.dto.input.VerifyEmailAuthInput;
import com.example.memberservice.auth.email.util.AuthMailSender;
import com.example.memberservice.common.exception.BusinessException;
import com.example.memberservice.common.exception.ErrorCode;
import com.example.memberservice.common.security.model.vo.Provider;
import com.example.memberservice.member.model.entity.Members;
import com.example.memberservice.member.model.enums.Gender;
import com.example.memberservice.member.model.enums.MemberRole;
import com.example.memberservice.member.repository.MemberJpaRepository;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class EmailAuthServiceTest {

    @MockitoBean
    EmailAuthRedisRepository emailAuthRedisRepository;

    @MockitoBean
    AuthMailSender authMailSender;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    EmailAuthService emailAuthService;

    @MockitoBean
    KafkaAdmin kafkaAdmin;

    @BeforeEach
    public void init() {
        emailAuthService = new EmailAuthService(
            authMailSender,
            emailAuthRedisRepository,
            memberJpaRepository
        );
    }

    @AfterEach
    void tearDown() {
        memberJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("멤버가 존재하면 이메일 인증 코드 전송 성공")
    void sendAuthMail_success() {
        // given
        Members member = memberJpaRepository.save(createMember("code123", "test@naver.com", "nick"));
        CreateEmailAuthInput input = CreateEmailAuthInput.builder()
            .memberRole(MemberRole.FREELANCER)
            .to("test@gmail.com")
            .memberCode(member.getCode())
            .build();

        // when
        emailAuthService.sendAuthMail(input);

        // then: 이메일 전송 호출 검증
        verify(authMailSender, times(1)).sendAuthCode(
            eq(MemberRole.FREELANCER),
            eq("test@gmail.com"),
            any(String.class)  // 랜덤 코드라 any()
        );

        // then: Redis 저장 호출 검증
        verify(emailAuthRedisRepository, times(1))
            .saveAuthCode(eq(MemberRole.FREELANCER), eq("code123"), any(String.class));
    }

    @Test
    @DisplayName("멤버가 존재하지 않으면 이메일 인증 코드 전송 실패")
    void sendAuthMail_memberNotFound() {
        // given
        CreateEmailAuthInput input = CreateEmailAuthInput.builder()
            .memberRole(MemberRole.FREELANCER)
            .to("test@gmail.com")
            .memberCode("not_exist")
            .build();

        // expect
        BusinessException ex = assertThrows(BusinessException.class,
            () -> emailAuthService.sendAuthMail(input));

        assertEquals(ErrorCode.MEMBER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("인증 코드 검증 성공 시 true 리턴")
    void verifyAuthCode_success() {
        // given
        Members member = memberJpaRepository.save(createMember("verify001", "a@a.com", "nick"));

        VerifyEmailAuthInput input = VerifyEmailAuthInput.builder()
            .memberRole(MemberRole.FREELANCER)
            .memberCode(member.getCode())
            .authCode("123456")
            .build();

        // Redis Mock: 저장된 코드가 있다고 가정
        when(emailAuthRedisRepository.findAuthCodeByMemberCode(
            MemberRole.FREELANCER, "verify001"))
            .thenReturn(Optional.of("123456"));

        // when
        boolean result = emailAuthService.verifyAuthCode(input);

        // then
        assertTrue(result);

        verify(emailAuthRedisRepository).saveAuthVerification(
            MemberRole.FREELANCER, "verify001"
        );
        verify(emailAuthRedisRepository).deleteAuthCode(
            MemberRole.FREELANCER, "verify001"
        );
    }

    @Test
    @DisplayName("인증 코드가 없으면 EMAIL_VERIFICATION_NOT_FOUND 예외")
    void verifyAuthCode_notFound() {
        Members member = memberJpaRepository.save(createMember("verify002", "a@a.com", "nick"));

        VerifyEmailAuthInput input = VerifyEmailAuthInput.builder()
            .memberRole(MemberRole.FREELANCER)
            .memberCode(member.getCode())
            .authCode("333333")
            .build();

        when(emailAuthRedisRepository.findAuthCodeByMemberCode(
            MemberRole.FREELANCER, "verify002"))
            .thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
            () -> emailAuthService.verifyAuthCode(input));

        assertEquals(ErrorCode.EMAIL_VERIFICATION_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("코드 불일치 시 EMAIL_VERIFICATION_CODE_MISMATCH 예외")
    void verifyAuthCode_mismatch() {
        Members member = memberJpaRepository.save(createMember("verify003", "a@a.com", "nick"));

        VerifyEmailAuthInput input = VerifyEmailAuthInput.builder()
            .memberRole(MemberRole.FREELANCER)
            .memberCode(member.getCode())
            .authCode("111111")
            .build();

        when(emailAuthRedisRepository.findAuthCodeByMemberCode(
            MemberRole.FREELANCER, "verify003"))
            .thenReturn(Optional.of("222222"));

        BusinessException ex = assertThrows(BusinessException.class,
            () -> emailAuthService.verifyAuthCode(input));

        assertEquals(ErrorCode.EMAIL_VERIFICATION_CODE_MISMATCH, ex.getErrorCode());
    }



    private Members createMember(String code, String email, String nickName) {
        return Members.builder()
            .code(code)
            .nickName(nickName)
            .email(email)
            .phoneNumber("010-9876-5432")
            .birthDate(LocalDate.of(1993, 5, 16))
            .gender(Gender.FEMALE)
            .provider(Provider.KAKAO)
            .providerId("kakao-67890")
            .build();
    }
}