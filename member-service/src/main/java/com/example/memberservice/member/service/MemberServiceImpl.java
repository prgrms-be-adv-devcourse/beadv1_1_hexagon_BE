package com.example.memberservice.member.service;

import com.example.memberservice.auth.email.repository.EmailAuthRepository;
import com.example.memberservice.common.client.ContractServiceClient;
import com.example.memberservice.common.client.dto.response.ContractStateResponse;
import com.example.memberservice.common.exception.BusinessException;
import com.example.memberservice.common.exception.ErrorCode;
import com.example.memberservice.common.kafka.producer.MemberKafkaEventProducer;
import com.example.memberservice.member.model.enums.MemberRole;
import org.hexagon.core.dto.ResponseDto;
import com.example.memberservice.member.controller.dto.response.MemberGetResponse;
import com.example.memberservice.member.model.entity.Members;
import com.example.memberservice.member.repository.MemberJpaRepository;
import com.example.memberservice.member.service.mapper.MembersMapper;
import com.example.memberservice.member.service.model.dto.input.MemberCreateInput;
import com.example.memberservice.member.service.model.dto.input.MemberDeleteInput;
import com.example.memberservice.member.service.model.dto.input.MemberExistByNameInput;
import com.example.memberservice.member.service.model.dto.input.MemberGetInput;
import com.example.memberservice.member.service.model.dto.input.MemberUpdateInput;
import com.example.memberservice.member.service.model.dto.input.MemberUpdateRoleStateInput;
import com.example.memberservice.member.service.model.vo.ApiMemberInfo;
import com.example.memberservice.member.service.model.vo.MemberRating;
import com.example.memberservice.member.service.model.vo.MemberTag;
import com.example.memberservice.member.service.util.RequestURIGenerator;
import com.example.memberservice.socialmember.entity.SocialMembers;
import com.example.memberservice.socialmember.repository.SocialMemberJpaRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hexagon.core.events.member.MemberCreatedEvent;
import org.hexagon.core.events.member.MemberDeletedClientRoleEvent;
import org.hexagon.core.events.member.MemberDeletedEvent;
import org.hexagon.core.events.member.MemberDeletedFreelancerRoleEvent;
import org.hexagon.core.events.member.MemberUpdatedEvent;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberJpaRepository memberJpaRepository;

    private final SocialMemberJpaRepository socialMemberJpaRepository;

    // 멤버 조회에서 태그 정보와 평가 정보를 받아올 RestTemplate
    private final RestTemplate restTemplate;

    //멤버 생성 및 업데이트 시 이벤트 발생 주체
    private final MemberKafkaEventProducer memberKafkaEventProducer;

    private final RequestURIGenerator requestURIGenerator;

    private final EmailAuthRepository emailAuthRepository;

    private final ContractServiceClient contractServiceClient;

    //외부 API를 2개나 타기에 Transactional을 해주지 않습니다.
    @Override
    public MemberGetResponse getMemberByCode(MemberGetInput input) {

        String findMemberCode = (input.xCode() != null && !input.xCode().isBlank())
            ? input.xCode()
            : input.paramCode();

        if (findMemberCode == null || findMemberCode.isBlank()) {
            throw new BusinessException(ErrorCode.NOT_CONTAINS_MEMBER_CODE);
        }

        Members existMembers = findMembers(findMemberCode);

        ApiMemberInfo memberInfo = MembersMapper.toGetDto(existMembers);

        //평가에 대한 정보를 받는 API 를 연결
        // /api/ratings/{findMemberCode}
        MemberRating memberRating = null;

        List<MemberTag> memberTags = null;

        memberRating = getMemberRating(findMemberCode);

        memberTags = getMemberTags(findMemberCode);

        return new MemberGetResponse(memberInfo, memberRating, memberTags);
    }

    @Override
    @Transactional
    public void createMember(MemberCreateInput input) {

        //이미 회원가입을 한 멤버 Code인지 확인
        if (memberJpaRepository.existsByCode(input.memberCode())) {
            throw new BusinessException(ErrorCode.MEMBER_ALREADY_EXISTS);
        }

        //회원 가입을 하기 전 이미 해당 nick name을 사용하는 사람이 있는 지 확인.
        checkNickNameDuplicate(input.memberCode(), input.name());

        //소셜 로그인을 통한 socialMember 찾기 없으면 회원가입이 불가.
        SocialMembers socialMembers = socialMemberJpaRepository.findSocialMembersByCode(
                input.memberCode())
            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        //생성할 멤버 생성
        Members newMember = Members.builder()
            .nickName(input.name())
            .gender(input.gender())
            .code(input.memberCode())
            .birthDate(input.birthDate())
            .phoneNumber(input.phoneNumber())
            .email(socialMembers.getEmail())
            .provider(socialMembers.getProvider())
            .providerId(socialMembers.getProviderId())
            .build();

        Members savedMember = memberJpaRepository.save(newMember);

        // Kafka Event 발송.
        memberKafkaEventProducer.sendCreatedEvent(new MemberCreatedEvent(savedMember.getCode()));

    }

    @Override
    @Transactional
    public void updateMember(MemberUpdateInput input) {
        //회원 가입을 하기 전 이미 해당 nick name을 사용하는 사람이 있는 지 확인.
        checkNickNameDuplicate(input.memberCode(), input.name());

        Members existMember = findMembers(input.memberCode());

        MembersMapper.toApply(existMember, input);

        Members updatedMember = memberJpaRepository.save(existMember);

        memberKafkaEventProducer.sendUpdatedEvent(
            new MemberUpdatedEvent(updatedMember.getCode(), updatedMember.getNickName()));
    }


    @Override
    @Transactional
    public void updateMemberRoleState(MemberUpdateRoleStateInput input) {

        String memberCode = input.memberCode();
        MemberRole inputRole = input.role();

        Members existMember = findMembers(input.memberCode());

        //이메일 인증 내역이 있는지 확인
        if (!emailAuthRepository.existVerificationByMemberCode(inputRole, memberCode)) {
            throw new BusinessException(ErrorCode.EMAIL_VERIFICATION_NEED);
        }

        // Register 변경이 불가능한 경우 안하고 넘어가기.
        // Register가 불가능한 경우는 보통 이미 자격이 있거나 admin 이거나라서 상태 변화를 일으키지 않도록.
        if (!existMember.canRegisterRoleState(inputRole)) {
            log.info("이미 자격이 있습니다.");
            return;
        }

        existMember.registerRoleState(inputRole);

        Members updatedMember = memberJpaRepository.save(existMember);
    }

    @Override
    @Transactional
    public void deleteMemberRoleState(MemberUpdateRoleStateInput input) {

        String memberCode = input.memberCode();
        MemberRole inputRole = input.role();

        Members existMember = findMembers(input.memberCode());

        //TODO(Contract에서 Internal API 구현시 해제)
        if (hasContractForRole(existMember, inputRole)) {
            throw new BusinessException(ErrorCode.CONTRACT_EXISTS);
        }
        // delete 변경이 불가능한 경우 안하고 넘어가기.
        // delete가 불가능한 경우는 이미 해당 자격이 없거나 admin 이거나라서 상태 변화를 일으키지 않도록.
        if (!existMember.canDeleteRoleState(inputRole)) {
            return;
        }
        existMember.deleteRoleState(inputRole);

        Members updatedMember = memberJpaRepository.save(existMember);

        if (inputRole.equals(MemberRole.CLIENT)){
            memberKafkaEventProducer.sendDeletedClientRoleEvent(new MemberDeletedClientRoleEvent(updatedMember.getCode()));
        }else{
            memberKafkaEventProducer.sendDeletedFreelancerRoleEvent(new MemberDeletedFreelancerRoleEvent(updatedMember.getCode()));
        }
    }


    @Override
    public void deleteMember(MemberDeleteInput input) {
        Members existMember = findMembers(input.memberCode());

        existMember.deletedMember();
        //TODO(Contract에서 Internal API 구현시 해제)
        if (hasContractForRole(existMember, MemberRole.BOTH)) {
            throw new BusinessException(ErrorCode.CONTRACT_EXISTS);
        }

        Members deleteMember = memberJpaRepository.save(existMember);

        memberKafkaEventProducer.sendDeletedEvent(new MemberDeletedEvent(deleteMember.getCode()));
    }

    @Override
    public void existMemberByNickName(MemberExistByNameInput input) {
        checkNickNameDuplicate(input.memberCode(), input.name());
    }

    private List<MemberTag> getMemberTags(String findMemberCode) {
        List<MemberTag> memberTags;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-CODE", findMemberCode); // 원하는 값 설정
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<ResponseDto<List<MemberTag>>> response = restTemplate.exchange(
                requestURIGenerator.gettagsUri(findMemberCode),
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
            );

            memberTags = Objects.requireNonNull(response.getBody()).data();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return memberTags;
    }

    private MemberRating getMemberRating(String findMemberCode) {
        MemberRating memberRating;
        try {
            ResponseEntity<ResponseDto<MemberRating>> response = restTemplate.exchange(
                requestURIGenerator.getRatingUri(findMemberCode),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
            );

            memberRating = Objects.requireNonNull(response.getBody()).data();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return memberRating;
    }

    private Members findMembers(String code) {
        return memberJpaRepository.findMembersByCodeAndIsDeletedFalse(code)
            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private void checkNickNameDuplicate(String code, String nickname) {
        memberJpaRepository.findMembersByNickName(nickname)
            .ifPresent(member -> {
                if (!code.equals(member.getCode())) {
                    throw new BusinessException(ErrorCode.NICKNAME_ALREADY_EXISTS);
                }
            });
    }

    private boolean validContract(ContractStateResponse contractStateResponse) {
        return !(contractStateResponse.isClient() && contractStateResponse.isFreelancer());
    }

    private boolean hasContractForRole(Members existMember, MemberRole inputRole) {
        ContractStateResponse response = contractServiceClient.existContractByRole(
            existMember.getCode()).data();

        return switch (inputRole) {
            case FREELANCER -> response.isFreelancer();
            case CLIENT -> response.isClient();
            case BOTH -> response.isFreelancer() || response.isClient();
            default -> false;
        };
    }

}
