package com.example.profileservice.tag.service;

import com.example.profileservice.common.model.vo.ErrorCode;
import com.example.profileservice.common.model.vo.KafkaProducer;
import com.example.profileservice.common.model.vo.exception.CustomException;
import com.example.profileservice.tag.model.dto.request.TagRequest;
import com.example.profileservice.tag.model.dto.response.TagResponse;
import com.example.profileservice.tag.model.entity.MemberTagEntity;
import com.example.profileservice.tag.model.entity.TagEntity;
import com.example.profileservice.tag.repository.MemberTagRepository;
import com.example.profileservice.tag.repository.TagRepository;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hexagon.core.events.profile.ProfileChangedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

    private final TagRepository tagRepository;
    private final MemberTagRepository memberTagRepository;
    private final KafkaProducer kafkaProducer;

    // Search Service에서 사용할 토픽 이름
    @Value("${topics.tag-events:tag-events}")
    private String tagTopic;

    @Value("${kafka.topic.profile-changed.name}")
    private String profileChangedTopic;

    // 전체 태그 목록 조회
    public List<TagResponse> getAllTags() {
        return tagRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 새로운 기술 태그를 등록
    @Transactional
    public TagResponse createTag(TagRequest request) {
        // 1. 중복 등록 방지
        if (tagRepository.existsBySkillIgnoreCase(request.skill())) {
            throw new CustomException(ErrorCode.TAG_ALREADY_EXISTS);
        }

        // 2. TagEntity 생성 및 저장
        TagEntity newTag = TagEntity.builder()
                .skill(request.skill())
                .build();

        TagEntity savedTag = tagRepository.save(newTag);

        TagResponse response = toResponse(savedTag);

        return toResponse(savedTag);
    }

    // 특정 회원이 등록한 태그 목록을 조회
    public List<TagResponse> getMyTags(String memberCode) {
        // 1. 회원의 모든 MemberTagEntity 조회
        List<MemberTagEntity> memberTags = memberTagRepository.findAllByMemberCode(memberCode);

        if (memberTags.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 연결된 TagEntity의 code를 추출
        List<String> tagCodes = memberTags.stream()
                .map(MemberTagEntity::getTagCode)
                .collect(Collectors.toList());

        // 3. 추출된 코드를 사용하여 TagEntity 목록을 한 번에 조회
        List<TagEntity> tags = tagRepository.findAllByCodeIn(tagCodes);

        return tags.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 마이페이지에 특정 태그를 연결
    @Transactional
    public void linkMemberTag(String memberCode, String tagCode) {
        // 1. 태그 존재 여부 확인
        TagEntity tag = tagRepository.findByCode(tagCode)
                .orElseThrow(() -> new CustomException(ErrorCode.TAG_NOT_FOUND));

        // 2. 중복 연결 방지
        if (memberTagRepository.existsByMemberCodeAndTagCode(memberCode, tagCode)) {
            return;
        }

        // 3. MemberTagEntity 생성 및 저장
        MemberTagEntity memberTag = MemberTagEntity.create(memberCode, tagCode);

        memberTagRepository.save(memberTag);


        // 현재 회원의 태그 목록 전체를 조회하여 TagInitEvent 생성
//        List<Tag> memberTags = getMyTags(memberCode).stream()
//                .map(t -> new Tag(t.tagCode(), t.skill())) // TagResponse -> core.vo.Tag 변환
//                .toList();
//
//        TagInitEvent event = new TagInitEvent(memberTags);
//
//        // memberCode를 키로 사용하여 해당 회원의 데이터 변경을 알림
//        kafkaProducer.send(tagTopic, memberCode, event);

        // 프로필 생성/수정 이벤트 발행
        ProfileChangedEvent profileChangedEvent = new ProfileChangedEvent(memberCode);
        kafkaProducer.send(profileChangedTopic, memberCode, profileChangedEvent);
    }

    // 마이페이지에서 특정 태그 연결을 해제
    @Transactional
    public void unlinkMemberTag(String memberCode, String tagCode) {
        // 1. 연결된 MemberTagEntity 조회
        TagEntity tag = tagRepository.findByCode(tagCode)
                .orElseThrow(() -> new CustomException(ErrorCode.TAG_NOT_FOUND));

        MemberTagEntity memberTag = memberTagRepository.findByMemberCodeAndTagCode(memberCode, tagCode)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_TAG_NOT_FOUND));

        // 2. 삭제
        memberTagRepository.delete(memberTag);

        // 현재 회원의 태그 목록 전체를 조회하여 TagInitEvent 생성
//        List<Tag> memberTags = getMyTags(memberCode).stream()
//                .map(t -> new Tag(t.tagCode(), t.skill())) // TagResponse -> core.vo.Tag 변환
//                .toList();
//
//        TagInitEvent event = new TagInitEvent(memberTags);
//
//        // memberCode를 키로 사용하여 해당 회원의 데이터 변경을 알림
//        kafkaProducer.send(tagTopic, memberCode, event);
    }

    // 회원 태그 목록 동기화
    @Transactional
    public void syncMemberTags(String memberCode, List<String> requestedTagCodesList) {
        // 1. 요청된 태그 코드가 DB에 실제로 존재하는지 일괄 확인 (데이터 무결성)
        List<TagEntity> validTags = tagRepository.findAllByCodeIn(requestedTagCodesList);
        Set<String> existingTagCodesInDb = validTags.stream()
                .map(TagEntity::getCode)
                .collect(Collectors.toSet());

        // 2. 현재 회원의 태그 코드 목록 조회
        Set<String> currentTagCodes = memberTagRepository.findAllByMemberCode(memberCode).stream()
                .map(MemberTagEntity::getTagCode)
                .collect(Collectors.toSet());

        // 3. 요청된 태그 코드 목록 (Set으로 변환하여 비교 용이하게)
        Set<String> requestedTagCodes = new HashSet<>(requestedTagCodesList);

        // 4. [삭제할 태그] 찾기: 현재 태그에는 있지만 요청 목록에는 없는 태그 (Current - Requested)
        Set<String> tagsToRemove = new HashSet<>(currentTagCodes);
        tagsToRemove.removeAll(requestedTagCodes);

        // 5. [추가할 태그] 찾기: 요청 목록에는 있지만 현재 태그에는 없는 태그 (Requested - Current)
        Set<String> tagsToAdd = new HashSet<>(requestedTagCodes);
        tagsToAdd.removeAll(currentTagCodes);

        // 6. DB에 없는 태그 코드는 추가 목록에서 제외 (요청은 왔지만 DB에 없는 경우 방지)
        tagsToAdd.retainAll(existingTagCodesInDb);

        // 7. 삭제 작업 실행
        if (!tagsToRemove.isEmpty()) {
            memberTagRepository.deleteAllByMemberCodeAndTagCodeIn(memberCode, tagsToRemove);
        }

        // 8. 추가 작업 실행
        if (!tagsToAdd.isEmpty()) {
            List<MemberTagEntity> newConnections = tagsToAdd.stream()
                    .map(tagCode -> MemberTagEntity.builder()
                            .memberCode(memberCode)
                            .tagCode(tagCode)
                            .build())
                    .collect(Collectors.toList());

            memberTagRepository.saveAll(newConnections);
        }

        // 9. 이벤트 발행 (최종 동기화된 회원 태그 목록 전체 발행)
        // (tagsToRemove.isEmpty() && tagsToAdd.isEmpty()가 아닐 경우에만 발행하는 최적화 로직 추가 권장)
//        if (!tagsToRemove.isEmpty() || !tagsToAdd.isEmpty()) {
//            List<Tag> memberTags = getMyTags(memberCode).stream()
//                    .map(t -> new Tag(t.tagCode(), t.skill())) // TagResponse -> core.vo.Tag 변환
//                    .toList();
//
//            TagInitEvent event = new TagInitEvent(memberTags);
//
//            // memberCode를 키로 사용하여 해당 회원의 데이터 변경을 알림
//            kafkaProducer.send(tagTopic, memberCode, event);
//        }
    }

    // 기술명으로 태그 엔티티 조회 (이름으로 조회하고, 없을 경우 예외를 발생)
    public TagResponse getTagBySkill(String skill) {
        TagEntity tag = tagRepository.findBySkillIgnoreCase(skill)
                .orElseThrow(() -> new CustomException(ErrorCode.TAG_NOT_FOUND)); // TAG_NOT_FOUND 예외 사용

        return toResponse(tag);
    }

    // 멤버 모듈의 요청을 받아 해당 프리랜서의 모든 태그 연결 정보(MemberTagEntity)를 삭제
    @Transactional
    public void deleteMemberTagsByMemberCode(String memberCode) {
        log.info("프리랜서 등록 취소 - MemberTag 연결 정보 일괄 삭제 시작. memberCode: {}", memberCode);

        // 1. 일괄 Hard Delete 처리
        memberTagRepository.deleteAllByMemberCode(memberCode);

        // 2. 이벤트 발행
//        TagInitEvent event = new TagInitEvent(Collections.emptyList());
//
//        // memberCode를 키로 사용하여 해당 회원의 데이터 변경을 알림
//        kafkaProducer.send(tagTopic, memberCode, event);

        log.info("MemberTag 연결 정보 일괄 삭제 및 빈 태그 목록 이벤트 발행 완료. memberCode: {}", memberCode);
    }

    // 여러 코드를 한 번에 이름으로 변환
    public List<TagResponse> getTagsByCodes(List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return Collections.emptyList();
        }

        return tagRepository.findAllByCodeIn(codes).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private TagResponse toResponse(TagEntity entity) {
        return new TagResponse(entity.getCode(), entity.getSkill());
    }
}
