package com.example.profileservice.resume.service;

import com.example.profileservice.common.model.vo.ErrorCode;
import com.example.profileservice.common.model.vo.exception.CustomException;
import com.example.profileservice.common.model.vo.util.MemberExistOutput;
import com.example.profileservice.common.model.vo.util.MemberFeignClient;
import com.example.profileservice.experience.model.dto.request.ExperienceRequest;
import com.example.profileservice.experience.model.dto.response.ExperienceResponse;
import com.example.profileservice.experience.model.entity.ExperienceEntity;
import com.example.profileservice.experience.repository.ExperienceRepository;
import com.example.profileservice.resume.model.dto.request.ResumeCreateRequest;
import com.example.profileservice.resume.model.dto.request.ResumeUpdateRequest;
import com.example.profileservice.resume.model.dto.response.ResumeDetailResponse;
import com.example.profileservice.resume.model.dto.response.ResumeSimpleResponse;
import com.example.profileservice.resume.model.entity.ResumeEntity;
import com.example.profileservice.resume.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final ExperienceRepository experienceRepository;
    private final MemberFeignClient memberFeignClient;

    // 회원이 작성한 모든 이력서를 조회
    public List<ResumeSimpleResponse> getMyResumes(String memberCode) {
        List<ResumeEntity> resumes = resumeRepository.findActiveListByMemberCode(memberCode);

        return resumes.stream()
                .map(this::toSimpleResponse)
                .collect(Collectors.toList());
    }

    // 새로운 이력서를 등록하고, 최초 등록 시 이벤트 발행
    @Transactional
    public ResumeDetailResponse createResume(String memberCode, ResumeCreateRequest request) {
        // 1. 회원 유효성 검증 로직
        validateMemberCode(memberCode);

        // 2. 이력서 엔티티 생성 및 저장
        ResumeEntity resume = ResumeEntity.builder()
                .memberCode(memberCode)
                .title(request.title())
                .body(request.body())
                .link(request.link())
                .build();

        resumeRepository.save(resume);

        return toDetailResponse(resume, List.of());
    }

    // 특정 이력서(경력/경험 포함)의 상세 정보를 조회 (경험 목록 포함)
    public ResumeDetailResponse getResumeDetail(String memberCode, String resumeCode) {
        // 1. 이력서 존재 및 권한 확인
        ResumeEntity resume = getResumeOrThrow(resumeCode, memberCode);

        // 2. 경력/경험 목록 조회
        List<ExperienceEntity> experiences = experienceRepository.findAllByResumeCodeAndIsDeletedFalseOrderByStartedAtDesc(resumeCode);
        List<ExperienceResponse> experienceResponses = experiences.stream()
                .map(this::toExperienceResponse)
                .collect(Collectors.toList());

        return toDetailResponse(resume, experienceResponses);
    }

    // 특정 이력서의 내용을 수정
    @Transactional
    public ResumeDetailResponse updateResume(String memberCode, String resumeCode, ResumeUpdateRequest request) {
        // 1. 이력서 존재 및 권한 확인
        ResumeEntity resume = getResumeOrThrow(resumeCode, memberCode);

        // 2. 수정
        resume.update(request.title(), request.body(), request.link());

        // 3. 경력/경험 목록을 다시 조회하여 응답에 포함
        List<ExperienceEntity> experiences = experienceRepository.findAllByResumeCodeAndIsDeletedFalseOrderByStartedAtDesc(resumeCode);
        List<ExperienceResponse> experienceResponses = experiences.stream()
                .map(this::toExperienceResponse)
                .collect(Collectors.toList());

        return toDetailResponse(resume, experienceResponses);
    }

    // 특정 이력서를 삭제(Soft Delete) 처리
    @Transactional
    public void deleteResume(String memberCode, String resumeCode) {
        // 1. 이력서 존재 및 권한 확인
        ResumeEntity resume = getResumeOrThrow(resumeCode, memberCode);

        // 2. Soft Delete 처리
        resume.delete();
    }

    // 특정 이력서에 경력/경험을 등록
    @Transactional
    public ExperienceResponse createExperience(String memberCode, String resumeCode, ExperienceRequest request) {
        // 1. 이력서 존재 및 권한 확인 (해당 이력서에 경험을 추가할 권한이 있는지 확인)
        getResumeOrThrow(resumeCode, memberCode);

        // 2. Experience 엔티티 생성 (팩토리 메서드 사용)
        ExperienceEntity experience = ExperienceEntity.create(
                resumeCode,
                request.title(),
                request.organization(),
                request.description(),
                request.startedAt(),
                request.endedAt()
        );
        experienceRepository.save(experience);

        return toExperienceResponse(experience);
    }

    // 특정 경력/경험 항목을 수정
    @Transactional
    public ExperienceResponse updateExperience(String memberCode, String resumeCode, String experienceCode, ExperienceRequest request) {
        // 1. 이력서 존재 및 권한 확인 (이력서가 유효한지 확인)
        getResumeOrThrow(resumeCode, memberCode);

        // 2. 경력/경험 존재 확인 및 이력서 코드 일치 확인
        ExperienceEntity experience = experienceRepository.findByCodeAndResumeCodeAndIsDeletedFalse(experienceCode, resumeCode)
                .orElseThrow(() -> new CustomException(ErrorCode.EXPERIENCE_NOT_FOUND));

        // 3. 수정
        experience.update(
                request.title(),
                request.organization(),
                request.description(),
                request.startedAt(),
                request.endedAt()
        );

        return toExperienceResponse(experience);
    }

    // 특정 경력/경험 항목을 삭제(Soft Delete) 처리
    @Transactional
    public void deleteExperience(String memberCode, String resumeCode, String experienceCode) {
        // 1. 이력서 존재 및 권한 확인 (이력서가 유효한지 확인)
        getResumeOrThrow(resumeCode, memberCode);

        // 2. 경력/경험 존재 확인 및 이력서 코드 일치 확인
        ExperienceEntity experience = experienceRepository.findByCodeAndResumeCodeAndIsDeletedFalse(experienceCode, resumeCode)
                .orElseThrow(() -> new CustomException(ErrorCode.EXPERIENCE_NOT_FOUND));

        // 3. Soft Delete 처리
        experience.delete();
    }

    // 회원 코드 유효성을 검증하는 헬퍼 메서드
    private void validateMemberCode(String memberCode) {
        // 1. Feign Client 호출
        ResponseDto<MemberExistOutput> response = memberFeignClient.existMemberByCode(List.of(memberCode));

        // 2. 응답 DTO의 성공/실패 여부 확인
        if (response.code() != 0) {
            return;
        }

        // 3. 응답 데이터(data)의 유효성 확인
        if (response.data() == null) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        MemberExistOutput existOutput = response.data();

        // 4. 존재하지 않는 회원 코드가 있는지 확인
        if (!existOutput.notExists().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_MEMBER_CODE);
        }
    }

    // resumeCode와 memberCode를 사용하여 이력서를 조회하고, 없거나 권한이 없으면 예외를 발생
    private ResumeEntity getResumeOrThrow(String resumeCode, String memberCode) {
        ResumeEntity resume = resumeRepository.findByCodeAndIsDeletedFalse(resumeCode)
                .orElseThrow(() -> new CustomException(ErrorCode.RESUME_NOT_FOUND));

        if (!resume.isOwnedBy(memberCode)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_RESUME_ACCESS);
        }
        return resume;
    }

    // 멤버 모듈의 요청을 받아 해당 프리랜서의 모든 활성 이력서 및 종속된 경력/경험을 논리적으로 삭제
    @Transactional
    public List<String> deleteResumesByMemberCode(String memberCode) {
        log.info("프리랜서 등록 취소 - Resume/Experience 일괄 삭제 시작. memberCode: {}", memberCode);

        // 1. 해당 회원의 모든 활성 이력서 조회
        List<ResumeEntity> resumesToDelete = resumeRepository.findActiveListByMemberCode(memberCode);

        if (resumesToDelete.isEmpty()) {
            log.info("삭제할 활성 Resume가 없습니다. memberCode: {}", memberCode);
            return List.of();
        }

        // 2. 삭제할 이력서 코드 목록 추출
        List<String> resumeCodesToDelete = resumesToDelete.stream()
                .map(ResumeEntity::getCode)
                .collect(Collectors.toList());

        // 3. 종속된 모든 경력/경험 항목 조회
        List<ExperienceEntity> experiencesToDelete = experienceRepository.findAllByResumeCodeInAndIsDeletedFalse(resumeCodesToDelete);

        // 4. 경력/경험 Soft Delete 처리
        experiencesToDelete.forEach(ExperienceEntity::delete);
        experienceRepository.saveAll(experiencesToDelete);
        log.info("삭제된 Experience 개수: {}", experiencesToDelete.size());

        // 5. 이력서 Soft Delete 처리
        resumesToDelete.forEach(ResumeEntity::delete);
        resumeRepository.saveAll(resumesToDelete);
        log.info("삭제된 Resume 개수: {}", resumesToDelete.size());

        // 이력서 삭제는 Search 인덱스에 영향을 주지 않으므로 개별 이벤트는 생략하고, 최종 이벤트는 Member 모듈에서 처리
        return resumeCodesToDelete;
    }

    private ResumeSimpleResponse toSimpleResponse(ResumeEntity entity) {
        return new ResumeSimpleResponse(
                entity.getCode(),
                entity.getTitle(),
                entity.getCreatedAt()
        );
    }

    private ResumeDetailResponse toDetailResponse(ResumeEntity entity, List<ExperienceResponse> experiences) {
        return new ResumeDetailResponse(
                entity.getCode(),
                entity.getTitle(),
                entity.getBody(),
                entity.getLink(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                experiences
        );
    }

    private ExperienceResponse toExperienceResponse(ExperienceEntity entity) {
        return new ExperienceResponse(
                entity.getCode(),
                entity.getTitle(),
                entity.getOrganization(),
                entity.getDescription(),
                entity.getStartedAt(),
                entity.getEndedAt()
        );
    }
}
