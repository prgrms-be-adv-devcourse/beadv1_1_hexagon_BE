package com.example.memberservice.member.service;


import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.memberservice.auth.email.repository.EmailAuthRepository;
import com.example.memberservice.common.client.ContractServiceClient;
import com.example.memberservice.common.client.dto.response.ContractStateResponse;
import com.example.memberservice.common.exception.BusinessException;
import com.example.memberservice.common.exception.ErrorCode;
import com.example.memberservice.common.kafka.producer.MemberKafkaEventProducer;
import com.example.memberservice.common.security.model.vo.Provider;
import com.example.memberservice.member.model.entity.Members;
import com.example.memberservice.member.model.enums.Gender;
import com.example.memberservice.member.model.enums.MemberRole;
import com.example.memberservice.member.repository.MemberJpaRepository;
import com.example.memberservice.member.service.model.dto.input.MemberCreateInput;
import com.example.memberservice.member.service.model.dto.input.MemberDeleteInput;
import com.example.memberservice.member.service.model.dto.input.MemberExistByNameInput;
import com.example.memberservice.member.service.model.dto.input.MemberGetInput;
import com.example.memberservice.member.service.model.dto.input.MemberUpdateInput;
import com.example.memberservice.member.service.model.dto.input.MemberUpdateRoleStateInput;
import com.example.memberservice.member.service.util.RequestURIGenerator;
import com.example.memberservice.socialmember.entity.SocialMembers;
import com.example.memberservice.socialmember.repository.SocialMemberJpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.hexagon.core.dto.ResponseDto;
import org.hexagon.core.events.member.MemberCreatedEvent;
import org.hexagon.core.events.member.MemberUpdatedEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MemberServiceImplTest {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private SocialMemberJpaRepository socialMemberJpaRepository;

    @Autowired
    private MemberServiceImpl service;

    @MockitoBean
    private RestTemplate restTemplate; // 외부 API는 Mock

    @MockitoBean
    private MemberKafkaEventProducer memberKafkaEventProducer;

    @MockitoBean
    private RequestURIGenerator requestURIGenerator;

    @MockitoBean
    private KafkaAdmin kafkaAdmin;

    @MockitoBean
    private EmailAuthRepository emailAuthRepository;

    @MockitoBean
    private ContractServiceClient contractServiceClient;

    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberService = new MemberServiceImpl(
            memberJpaRepository,
            socialMemberJpaRepository,
            restTemplate,
            memberKafkaEventProducer,
            requestURIGenerator,
            emailAuthRepository,
            contractServiceClient
        );
    }

    @AfterEach
    void tearDown() {
        memberJpaRepository.deleteAllInBatch();
        socialMemberJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("getMemberByCode: 코드 없으면 예외")
    void getMemberByCode_noCode() {
        MemberGetInput input = new MemberGetInput("", "");

        assertThatThrownBy(() -> service.getMemberByCode(input))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.NOT_CONTAINS_MEMBER_CODE.getMessage());
    }

    @Nested
    class CreateMemberTests {

        @Test
        @DisplayName("createMember: 이미 존재하면 예외")
        void createMember_already() {
            Members m = createMember();
            memberJpaRepository.save(m);

            MemberCreateInput input = new MemberCreateInput(m.getCode(), "nick", "010100200",
                LocalDate.now(), Gender.MAN);
            assertThatThrownBy(() -> service.createMember(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.MEMBER_ALREADY_EXISTS.getMessage());
        }

        @Test
        @DisplayName("createMember: 닉네임 중복시 예외")
        void createMember_nicknameDup() {
            Members m = createMember();
            memberJpaRepository.save(m);

            MemberCreateInput input = new MemberCreateInput("c1", m.getNickName(), "010100200",
                LocalDate.now(), Gender.MAN);

            assertThatThrownBy(() -> service.createMember(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.NICKNAME_ALREADY_EXISTS.getMessage());
        }

        @Test
        @DisplayName("createMember: 소셜멤버 없으면 예외")
        void createMember_noSocialMember() {
            MemberCreateInput input = new MemberCreateInput("who", "nick", "010100200", LocalDate.now(),
                Gender.MAN);

            assertThatThrownBy(() -> service.createMember(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("createMember: 회원가입 성공시 이벤트 발송")
        void createMember_success_andEventPublish() {
            // Given
            SocialMembers socialMembers = SocialMembers.builder()
                .email("e@ex.com").provider(Provider.NAVER).providerId("pid").build();
            SocialMembers save = socialMemberJpaRepository.save(socialMembers);

            MemberCreateInput input = new MemberCreateInput(save.getCode(), "new", "01022223333",
                LocalDate.now(), Gender.MAN);
            given(memberKafkaEventProducer.sendCreatedEvent(any(MemberCreatedEvent.class)))
                .willReturn(CompletableFuture.completedFuture(null));

            // When
            service.createMember(input);

            // Then
            Members saved = memberJpaRepository.findByCode(socialMembers.getCode()).get();
            assertThat(saved.getEmail()).isEqualTo("e@ex.com");
            verify(memberKafkaEventProducer).sendCreatedEvent(any(MemberCreatedEvent.class));
        }

    }


    @Nested
    class UpdateMemberTests {

        @Test
        @DisplayName("updateMember: 닉네임 중복이면 예외")
        void updateMember_dupNick() {
            Members m = createMember();
            Members m2 = createMember("worker");
            memberJpaRepository.saveAll(List.of(m, m2));

            MemberUpdateInput input = new MemberUpdateInput("c1", "worker", "01099998888",
                LocalDate.now(), Gender.MAN);

            assertThatThrownBy(() -> service.updateMember(input))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.NICKNAME_ALREADY_EXISTS.getMessage());
        }

        @Test
        @DisplayName("updateMember: 정상 업데이트 및 이벤트 발생")
        void updateMember_success() {
            Members m = createMember();
            memberJpaRepository.save(m);

            MemberUpdateInput input = new MemberUpdateInput(m.getCode(), "newNick", "01087901234",
                LocalDate.now(), Gender.MAN);
            given(memberKafkaEventProducer.sendUpdatedEvent(any(MemberUpdatedEvent.class)))
                .willReturn(CompletableFuture.completedFuture(null));

            service.updateMember(input);

            Members updated = memberJpaRepository.findByCode(m.getCode()).get();
            assertThat(updated.getNickName()).isEqualTo("newNick");
            verify(memberKafkaEventProducer).sendUpdatedEvent(any(MemberUpdatedEvent.class));
        }
    }


    @Test
    @DisplayName("deleteMember: 계약이 존재하는 경우 CONTRACT_EXISTS 예외 발생")
    void deleteMember_contract_exists() {
        Members m = createMember();
        memberJpaRepository.save(m);

        when(contractServiceClient.existContractByRole(anyString()))
            .thenReturn(ResponseDto.success(new ContractStateResponse(true, true))); // 둘 중 하나라도 true

        MemberDeleteInput input = new MemberDeleteInput(m.getCode());

        assertThatThrownBy(() -> service.deleteMember(input))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.CONTRACT_EXISTS.getMessage());
    }

    @Test
    @DisplayName("deleteMember: 삭제 플래그 업데이트")
    void deleteMember_success() {
        Members m = createMember();
        memberJpaRepository.save(m);

        when(contractServiceClient.existContractByRole(anyString())).thenReturn(ResponseDto.success(new ContractStateResponse(false, false)));

        MemberDeleteInput input = new MemberDeleteInput(m.getCode());
        service.deleteMember(input);

        Members updated = memberJpaRepository.findByCode(m.getCode()).get();

        assertThat(updated.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("existMemberByNickName: 닉네임 존재시 예외 발생, 안하면 통과")
    void existMemberByNickName_test() {

        memberJpaRepository.saveAll(List.of(createMember(), createMember("worker")));

        MemberExistByNameInput input = new MemberExistByNameInput("c1", "worker");
        assertThatThrownBy(() -> service.existMemberByNickName(input))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.NICKNAME_ALREADY_EXISTS.getMessage());

        MemberExistByNameInput input2 = new MemberExistByNameInput("c1", "새로운닉네임");
        service.existMemberByNickName(input2); // not throws
    }

        // updateMemberRoleState 테스트
        @Nested
        class UpdateRoleTests {

            @Test
            @DisplayName("NONE → CLIENT 요청 시 CLIENT 로 변경됨")
            void update_none_to_client() {
                Members m = createMember("code1", "tester1", MemberRole.NONE);
                memberJpaRepository.save(m);

                when(emailAuthRepository.existVerificationByMemberCode(any(), any())).thenReturn(true);

                memberService.updateMemberRoleState(
                    new MemberUpdateRoleStateInput(m.getCode(), MemberRole.CLIENT));

                assertThat(m.getRole()).isEqualTo(MemberRole.CLIENT);
            }

            @Test
            @DisplayName("NONE → CLIENT 요청 시 이메일 인증을 하지 않았을 경우 요청에 실패하고 EMAIL_VERIFICATION_NEED 예외 발생")
            void update_need_email_auth() {
                Members m = createMember("code1", "tester1", MemberRole.NONE);
                memberJpaRepository.save(m);

                when(emailAuthRepository.existVerificationByMemberCode(any(), any())).thenReturn(false);
                // when & then
                BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> memberService.updateMemberRoleState(
                        new MemberUpdateRoleStateInput(m.getCode(), MemberRole.CLIENT))
                );

                assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.EMAIL_VERIFICATION_NEED);
            }

            @Test
            @DisplayName("CLIENT → FREELANCER 요청 시 BOTH 로 변경됨")
            void update_client_to_freelancer() {
                Members m = createMember("code2", "tester2", MemberRole.CLIENT);
                memberJpaRepository.save(m);

                when(emailAuthRepository.existVerificationByMemberCode(any(), any())).thenReturn(true);

                memberService.updateMemberRoleState(
                    new MemberUpdateRoleStateInput(m.getCode(), MemberRole.FREELANCER));

                assertThat(m.getRole()).isEqualTo(MemberRole.BOTH);
            }

            @Test
            @DisplayName("FREELANCER → CLIENT 요청 시 BOTH 로 변경됨")
            void update_freelancer_to_client() {
                Members m = createMember("code3", "tester3", MemberRole.FREELANCER);
                memberJpaRepository.save(m);

                when(emailAuthRepository.existVerificationByMemberCode(any(), any())).thenReturn(true);

                memberService.updateMemberRoleState(
                    new MemberUpdateRoleStateInput(m.getCode(), MemberRole.CLIENT));

                assertThat(m.getRole()).isEqualTo(MemberRole.BOTH);
            }

            @Test
            @DisplayName("CLIENT → CLIENT 요청 시 변화 없음")
            void update_client_to_client() {
                Members m = createMember("code4", "tester4", MemberRole.CLIENT);
                memberJpaRepository.save(m);

                when(emailAuthRepository.existVerificationByMemberCode(any(), any())).thenReturn(true);

                memberService.updateMemberRoleState(
                    new MemberUpdateRoleStateInput(m.getCode(), MemberRole.CLIENT));

                assertThat(m.getRole()).isEqualTo(MemberRole.CLIENT);
            }

            @Test
            @DisplayName("FREELANCER → FREELANCER 요청 시 변화 없음")
            void update_freelancer_to_freelancer() {
                Members m = createMember("code5", "tester5", MemberRole.FREELANCER);
                memberJpaRepository.save(m);

                when(emailAuthRepository.existVerificationByMemberCode(any(), any())).thenReturn(true);

                memberService.updateMemberRoleState(
                    new MemberUpdateRoleStateInput(m.getCode(), MemberRole.FREELANCER));

                assertThat(m.getRole()).isEqualTo(MemberRole.FREELANCER);
            }

            @Test
            @DisplayName("ADMIN -> CLIENT update 요청 시 변화 없음")
            void update_admin_no_change() {
                Members m = createMember("code6", "tester6", MemberRole.ADMIN);
                memberJpaRepository.save(m);

                when(emailAuthRepository.existVerificationByMemberCode(any(), any())).thenReturn(true);

                memberService.updateMemberRoleState(
                    new MemberUpdateRoleStateInput(m.getCode(), MemberRole.CLIENT));

                assertThat(m.getRole()).isEqualTo(MemberRole.ADMIN);
            }
        }
        // deleteMemberRoleState 테스트
        @Nested
        class DeleteRoleTests {

            @BeforeEach
            void setUp() {
                when(contractServiceClient.existContractByRole(anyString())).thenReturn(
                    ResponseDto.success(new ContractStateResponse(false, false))
                );
            }

            @Test
            @DisplayName("CLIENT → CLIENT 삭제(NONE)")
            void deleteClient() {
                Members m = createMember("t1", "tester1", MemberRole.CLIENT);

                Members save = memberJpaRepository.save(m);

                memberService.deleteMemberRoleState(
                    new MemberUpdateRoleStateInput(m.getCode(), MemberRole.CLIENT));

                assertThat(m.getRole()).isEqualTo(MemberRole.NONE);
            }

            @Test
            @DisplayName("FREELANCER → FREELANCER 삭제(NONE)")
            void deleteFreelancer() {
                Members m = createMember("t1", "tester1", MemberRole.FREELANCER);

                Members save = memberJpaRepository.save(m);

                memberService.deleteMemberRoleState(
                    new MemberUpdateRoleStateInput(m.getCode(), MemberRole.FREELANCER));

                assertThat(m.getRole()).isEqualTo(MemberRole.NONE);
            }

            @Test
            @DisplayName("BOTH 상태에서 CLIENT 삭제 시 FREELANCER 로 변경됨")
            void delete_both_client() {
                Members m = createMember("code9", "tester9", MemberRole.BOTH);
                memberJpaRepository.save(m);

                memberService.deleteMemberRoleState(
                    new MemberUpdateRoleStateInput(m.getCode(), MemberRole.CLIENT));

                assertThat(m.getRole()).isEqualTo(MemberRole.FREELANCER);
            }

            @Test
            @DisplayName("ADMIN 삭제 요청 시 변화 없음")
            void delete_admin_no_change() {
                Members m = createMember("code10", "tester10", MemberRole.ADMIN);
                memberJpaRepository.save(m);

                memberService.deleteMemberRoleState(
                    new MemberUpdateRoleStateInput(m.getCode(), MemberRole.FREELANCER));

                assertThat(m.getRole()).isEqualTo(MemberRole.ADMIN);
            }

            @Test
            @DisplayName("NONE 이 CLIENT 삭제 요청 → 변화 없음")
            void delete_none() {
                Members m = createMember("code11", "tester11", MemberRole.NONE);
                memberJpaRepository.save(m);

                memberService.deleteMemberRoleState(
                    new MemberUpdateRoleStateInput(m.getCode(), MemberRole.CLIENT));

                assertThat(m.getRole()).isEqualTo(MemberRole.NONE);
            }


            @Test
            @DisplayName("현재 진행중인 계약이 있을 경우 CONTRACT_EXIST 예외 발생.")
            void contract_exists() {

                when(contractServiceClient.existContractByRole(anyString())).thenReturn(
                    ResponseDto.success(new ContractStateResponse(true, true))
                );

                Members m = createMember("code11", "tester11", MemberRole.NONE);
                memberJpaRepository.save(m);

                assertThatThrownBy(() -> service.deleteMemberRoleState(new MemberUpdateRoleStateInput(m.getCode(), MemberRole.CLIENT)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.CONTRACT_EXISTS.getMessage());
                assertThat(m.getRole()).isEqualTo(MemberRole.NONE);
            }
        }


    private Members createMember(String nickName) {
        return Members.builder()
            .code("c2")
            .nickName(nickName)
            .email("t@gmail.com")
            .gender(Gender.MAN)
            .birthDate(LocalDate.now())
            .phoneNumber("01093437373")
            .providerId("123123")
            .provider(Provider.NAVER)
            .build();
    }

    private Members createMember() {
        return Members.builder()
            .code("c1")
            .nickName("tester")
            .email("t2@gmail.com")
            .gender(Gender.MAN)
            .birthDate(LocalDate.now())
            .phoneNumber("01093437373")
            .providerId("123123")
            .provider(Provider.NAVER)
            .build();
    }

    private Members createMember(String code, String nickName, MemberRole role) {
        Members members = Members.builder()
            .code(code)
            .nickName(nickName)
            .email("t2@gmail.com")
            .gender(Gender.MAN)
            .birthDate(LocalDate.now())
            .phoneNumber("01093437373")
            .providerId("123123")
            .provider(Provider.NAVER)
            .role(role)
            .build();

        return members;
    }

}
