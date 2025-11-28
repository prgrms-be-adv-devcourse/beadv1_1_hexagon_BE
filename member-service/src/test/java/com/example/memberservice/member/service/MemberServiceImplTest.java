package com.example.memberservice.member.service;


import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.example.memberservice.common.exception.BusinessException;
import com.example.memberservice.common.exception.ErrorCode;
import com.example.memberservice.common.kafka.producer.MemberKafkaEventProducer;
import com.example.memberservice.common.security.model.vo.Provider;
import com.example.memberservice.member.entity.Members;
import com.example.memberservice.member.entity.vo.Gender;
import com.example.memberservice.member.repository.MemberJpaRepository;
import com.example.memberservice.member.service.model.dto.input.MemberCreateInput;
import com.example.memberservice.member.service.model.dto.input.MemberDeleteInput;
import com.example.memberservice.member.service.model.dto.input.MemberExistByNameInput;
import com.example.memberservice.member.service.model.dto.input.MemberGetInput;
import com.example.memberservice.member.service.model.dto.input.MemberUpdateInput;
import com.example.memberservice.member.service.model.dto.input.MemberUpdateWorkStateInput;
import com.example.memberservice.member.service.util.RequestURIGenerator;
import com.example.memberservice.socialmember.entity.SocialMembers;
import com.example.memberservice.socialmember.repository.SocialMemberJpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.hexagon.core.events.member.MemberCreatedEvent;
import org.hexagon.core.events.member.MemberUpdatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    @Autowired
    private MemberService memberService;




    @Test
    @DisplayName("getMemberByCode: 코드 없으면 예외")
    void getMemberByCode_noCode() {
        MemberGetInput input = new MemberGetInput("", "");

        assertThatThrownBy(() -> service.getMemberByCode(input))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.NOT_CONTAINS_MEMBER_CODE.getMessage());
    }

    @Test
    @DisplayName("createMember: 이미 존재하면 예외")
    void createMember_already() {
        Members m = createMember();
        memberJpaRepository.save(m);

        MemberCreateInput input = new MemberCreateInput("c1", "nick", "010100200", LocalDate.now(), Gender.MAN);
        assertThatThrownBy(() -> service.createMember(input))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.MEMBER_ALREADY_EXISTS.getMessage());
    }

    @Test
    @DisplayName("createMember: 닉네임 중복시 예외")
    void createMember_nicknameDup() {
        Members m = createMember();
        memberJpaRepository.save(m);

        MemberCreateInput input = new MemberCreateInput("c1", "tester", "010100200", LocalDate.now(), Gender.MAN);

        assertThatThrownBy(() -> service.createMember(input))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.NICKNAME_ALREADY_EXISTS.getMessage());
    }

    @Test
    @DisplayName("createMember: 소셜멤버 없으면 예외")
    void createMember_noSocialMember() {
        MemberCreateInput input = new MemberCreateInput("who", "nick", "010100200", LocalDate.now(), Gender.MAN);

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

        MemberCreateInput input = new MemberCreateInput(save.getCode(), "new", "01022223333", LocalDate.now(), Gender.MAN);
        given(memberKafkaEventProducer.sendCreatedEvent(any(MemberCreatedEvent.class)))
            .willReturn(CompletableFuture.completedFuture(null));

        // When
        service.createMember(input);

        // Then
        Members saved = memberJpaRepository.findByCode(socialMembers.getCode()).get();
        assertThat(saved.getEmail()).isEqualTo("e@ex.com");
        verify(memberKafkaEventProducer).sendCreatedEvent(any(MemberCreatedEvent.class));
    }

    @Test
    @DisplayName("updateMember: 닉네임 중복이면 예외")
    void updateMember_dupNick() {
        Members m = createMember();
        Members m2 = createMember("worker");
        memberJpaRepository.saveAll(List.of(m,m2));


        MemberUpdateInput input = new MemberUpdateInput("c1", "worker", "01099998888", LocalDate.now(), Gender.MAN);

        assertThatThrownBy(() -> service.updateMember(input))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.NICKNAME_ALREADY_EXISTS.getMessage());
    }

    @Test
    @DisplayName("updateMember: 정상 업데이트 및 이벤트 발생")
    void updateMember_success() {
        Members m = createMember();
        memberJpaRepository.save(m);

        MemberUpdateInput input = new MemberUpdateInput("c1", "newNick", "01087901234", LocalDate.now(), Gender.MAN);
        given(memberKafkaEventProducer.sendUpdatedEvent(any(MemberUpdatedEvent.class)))
            .willReturn(CompletableFuture.completedFuture(null));

        service.updateMember(input);

        Members updated = memberJpaRepository.findByCode("c1").get();
        assertThat(updated.getNickName()).isEqualTo("newNick");
        verify(memberKafkaEventProducer).sendUpdatedEvent(any(MemberUpdatedEvent.class));
    }

    @Test
    @DisplayName("updateMemberWorkState: canEnableWork true면 필드 변경 후 저장")
    void updateMemberWorkState_success() {
        Members m = createMember();
        memberJpaRepository.save(m);

        MemberUpdateWorkStateInput input = new MemberUpdateWorkStateInput("c1");
        service.updateMemberWorkState(input);

        Members updated = memberJpaRepository.findByCode("c1").get();
        assertThat(updated.getCanWork()).isTrue();
    }

    @Test
    @DisplayName("deleteMember: 삭제 플래그 업데이트")
    void deleteMember_success() {
        Members m = createMember();
        memberJpaRepository.save(m);

        MemberDeleteInput input = new MemberDeleteInput("c1");
        service.deleteMember(input);

        Members updated = memberJpaRepository.findByCode("c1").get();
        // deletedMember()가 삭제 플래그 처리하는 메서드라면 그에 맞게 검증
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


}
