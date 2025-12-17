package com.example.profileservice.selfPromotion.service;

import com.example.profileservice.common.model.vo.ErrorCode;
import com.example.profileservice.common.model.vo.KafkaProducer;
import com.example.profileservice.common.model.vo.exception.CustomException;
import com.example.profileservice.common.model.vo.util.MemberExistOutput;
import com.example.profileservice.common.model.vo.util.MemberFeignClient;
import com.example.profileservice.common.model.vo.util.MemberInfoOutput;
import com.example.profileservice.common.model.vo.util.S3ResourceService;
import com.example.profileservice.resume.repository.ResumeRepository;
import com.example.profileservice.selfPromotion.model.dto.request.SelfPromotionCreateRequest;
import com.example.profileservice.selfPromotion.model.dto.request.SelfPromotionUpdateRequest;
import com.example.profileservice.selfPromotion.model.dto.response.SelfPromotionResponse;
import com.example.profileservice.selfPromotion.model.entity.SelfPromotionEntity;
import com.example.profileservice.selfPromotion.repository.SelfPromotionRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hexagon.core.dto.ResponseDto;
import org.hexagon.core.events.selfpromotion.SelfPromotionDeletedEvent;
import org.hexagon.core.events.selfpromotion.SelfPromotionUpsertEvent;
import org.hexagon.core.vo.SelfPromotion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SelfPromotionService {

    private final SelfPromotionRepository selfPromotionRepository;
    private final ResumeRepository resumeRepository;
    private final KafkaProducer kafkaProducer;
    private final MemberFeignClient memberFeignClient;
    private final S3ResourceService s3ResourceService;

    // Search Service에서 사용할 토픽 이름
    @Value("${topics.selfpromotion-events:selfpromotion-events}")
    private String selfPromotionTopic;

    // 모든 활성 셀프 프로모션 게시글 목록을 최신순으로 조회
    public List<SelfPromotionResponse> getAllPromotions() {
        List<SelfPromotionEntity> promotions = selfPromotionRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc();

        return promotions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 특정 회원이 작성한 셀프 프로모션 게시글을 조회
    @Transactional(readOnly = true)
    public SelfPromotionResponse getMyPromotions(String memberCode) {
        // 단건 조회
        Optional<SelfPromotionEntity> myPromotion = selfPromotionRepository.findByMemberCodeAndIsDeletedFalse(memberCode);

        // 단일 객체 응답
        return myPromotion.map(this::toResponse).orElse(null);
    }

    // 특정 셀프 프로모션 게시글의 상세 정보를 조회
    public SelfPromotionResponse getPromotionDetail(String promotionCode) {
        SelfPromotionEntity promotion = selfPromotionRepository.findByCodeAndIsDeletedFalse(promotionCode)
                .orElseThrow(() -> new CustomException(ErrorCode.PROMOTION_NOT_FOUND));

        return toResponse(promotion);
    }

    // 새 셀프 프로모션 게시글을 등록
    @Transactional
    public SelfPromotionResponse createPromotion(String memberCode, SelfPromotionCreateRequest request) {
        // 1. 요청 회원 코드 유효성 검증
        validateMemberExists(memberCode);

        // 2. 이력서 유효성 검증
        validateResumeCode(request.resumeCode());

        // 3. 기존 활성 프로모션 확인 및 등록 차단
        selfPromotionRepository.findByMemberCodeAndIsDeletedFalse(memberCode)
                .ifPresent(existingPromotion -> {
                    throw new CustomException(ErrorCode.PROMOTION_ALREADY_EXISTS);
                });

        // 4. S3 리소스 영구 저장 및 Code 생성
        String pdfCode = null;
        if (request.pdfKey() != null && !request.pdfKey().isEmpty()) {
            pdfCode = UUID.randomUUID().toString(); // 새로운 Code 생성
            s3ResourceService.storeS3Keys(pdfCode, List.of(request.pdfKey())); // S3 모듈에 영구 저장 요청
        }

        // 5. SelfPromotion 엔티티 생성 및 저장
        SelfPromotionEntity promotion = SelfPromotionEntity.create(
                memberCode,
                request.title(),
                request.content(),
                request.paymentType(),
                request.unitAmount(),
                request.resumeCode(),
                pdfCode
        );

        selfPromotionRepository.save(promotion);

        SelfPromotionResponse response = toResponse(promotion);

        // 5. 이벤트 발행 (CREATE)
        SelfPromotion selfPromotionVo = toSelfPromotionVo(promotion);

        SelfPromotionUpsertEvent createdEvent = new SelfPromotionUpsertEvent(
                selfPromotionVo.code(), selfPromotionVo.title(), selfPromotionVo.content(),
                selfPromotionVo.memberCode(), selfPromotionVo.memberNickname(),
                selfPromotionVo.paymentType(), selfPromotionVo.payAmount(), selfPromotionVo.updatedAt()
        );

        kafkaProducer.send(selfPromotionTopic, response.promotionCode(), createdEvent);

        return toResponse(promotion);
    }

    // 특정 셀프 프로모션 게시글을 수정
    @Transactional
    public SelfPromotionResponse updatePromotion(String memberCode, String promotionCode, SelfPromotionUpdateRequest request) {
        // 1. 프로모션 존재 및 권한 확인
        SelfPromotionEntity promotion = getPromotionOrThrow(promotionCode, memberCode);

        // 2. 이력서 유효성 검증
        validateResumeCode(request.resumeCode());

        // 3. S3 리소스 동기화
        String currentPortfolioCode = promotion.getPdfKey();
        String newPortfolioCode = currentPortfolioCode;
        String updatedKey = request.pdfKey();

        // S3 모듈의 syncKeys/storeKeys가 List<String>을 받으므로, 단일 키를 List로 변환
        List<String> updatedKeysList = updatedKey != null && !updatedKey.isBlank() ? List.of(updatedKey) : List.of();

        if (currentPortfolioCode == null && !updatedKeysList.isEmpty()) {
            // 새롭게 pdf를 등록하는 경우
            newPortfolioCode = UUID.randomUUID().toString();
            s3ResourceService.storeS3Keys(newPortfolioCode, updatedKeysList);
        } else if (currentPortfolioCode != null) {
            // 기존 pdf를 수정/삭제하는 경우
            s3ResourceService.syncS3Keys(currentPortfolioCode, updatedKeysList);

            // 키가 완전히 제거되었다면(updatedKeysList.isEmpty()), code도 null로 설정
            if (updatedKeysList.isEmpty()) {
                newPortfolioCode = null;
            }
        }

        // 4. 수정 (요청에 포함된 필드만 업데이트하며, null이 올 경우 기존 값 유지)
        promotion.update(
                Optional.ofNullable(request.title()).orElse(promotion.getTitle()),
                Optional.ofNullable(request.content()).orElse(promotion.getContent()),
                Optional.ofNullable(request.paymentType()).orElse(promotion.getPaymentType()),
                Optional.ofNullable(request.unitAmount()).orElse(promotion.getUnitAmount()),
                request.resumeCode(),
                newPortfolioCode
        );

        SelfPromotionResponse response = toResponse(promotion);

        // 5. 이벤트 발행 (UPDATE)
        SelfPromotion selfPromotionVo = toSelfPromotionVo(promotion);

        SelfPromotionUpsertEvent updatedEvent = new SelfPromotionUpsertEvent(
                selfPromotionVo.code(), selfPromotionVo.title(), selfPromotionVo.content(),
                selfPromotionVo.memberCode(), selfPromotionVo.memberNickname(),
                selfPromotionVo.paymentType(), selfPromotionVo.payAmount(), selfPromotionVo.updatedAt()
        );

        kafkaProducer.send(selfPromotionTopic, response.promotionCode(), updatedEvent);

        return toResponse(promotion);
    }

    // 특정 셀프 프로모션 게시글을 삭제 처리
    @Transactional
    public void deletePromotion(String memberCode, String promotionCode) {
        // 1. 프로모션 존재 및 권한 확인
        SelfPromotionEntity promotion = getPromotionOrThrow(promotionCode, memberCode);

        // 2. 연결된 S3 파일 메타데이터 삭제
        // syncAttachments를 빈 키 리스트로 호출하여 DB에서 S3 Resource 메타데이터를 삭제하고 S3 오브젝트도 삭제
        if (promotion.getPdfKey() != null) {
            s3ResourceService.syncS3Keys(promotion.getPdfKey(), List.of());
        }

        // 3. Soft Delete 처리 및 이벤트 발행
        performSoftDeleteAndPublishEvent(promotion);
    }

    // 회원 코드로 닉네임을 조회하는 헬퍼 메서드
    private String getMemberNickname(String memberCode) {
        try {
            ResponseDto<MemberInfoOutput> response = memberFeignClient.getMemberInfoByCode(List.of(memberCode));

            if (response.data() == null || response.data().internalMemberInfos().isEmpty()) {
                // 회원 정보는 존재하지만(컨트롤러에서 예외 처리) 빈 목록일 경우
                log.warn("Member info not found for code: {}", memberCode);
                return "알 수 없음";
            }

            return response.data().internalMemberInfos().get(0).nickName();

        } catch (Exception e) {
            // 통신 오류 발생 시
            log.error("Failed to get member nickname for code: {}", memberCode, e);
            return "조회 실패";
        }
    }

    // promotionCode로 엔티티를 조회하고, 요청 memberCode와 작성자가 일치하는지 확인
    private SelfPromotionEntity getPromotionOrThrow(String promotionCode, String memberCode) {
        SelfPromotionEntity promotion = selfPromotionRepository.findByCodeAndIsDeletedFalse(promotionCode)
                .orElseThrow(() -> new CustomException(ErrorCode.PROMOTION_NOT_FOUND));

        if (!promotion.isOwner(memberCode)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_PROMOTION_ACCESS);
        }
        return promotion;
    }

    // 연결할 resumeCode의 유효성을 검증
    private void validateResumeCode(String resumeCode) {
        if (resumeCode != null && !resumeCode.isEmpty()) {
            boolean exists = resumeRepository.existsByCodeAndIsDeletedFalse(resumeCode);
            if (!exists) {
                throw new CustomException(ErrorCode.INVALID_RESUME_CODE_LINK);
            }
        }
    }

    // 요청 회원 코드가 유효한지 확인하는 메서드
    private void validateMemberExists(String memberCode) {
        ResponseDto<MemberExistOutput> response = memberFeignClient.existMemberByCode(List.of(memberCode));

        if (response.data() == null || response.data().notExists().contains(memberCode)) {
            // 존재하지 않는다면 CustomException을 던짐
            throw new CustomException(ErrorCode.INVALID_MEMBER_CODE);
        }
    }

    // 멤버 모듈의 요청을 받아 해당 프리랜서의 모든 활성 Self Promotion을 논리적으로 삭제
    @Transactional
    public void deletePromotionsByMemberCode(String memberCode) {
        log.info("프리랜서 등록 취소 - Self Promotion 삭제 시작. memberCode: {}", memberCode);

        // Optional로 조회
        selfPromotionRepository.findByMemberCodeAndIsDeletedFalse(memberCode)
                .ifPresent(this::performSoftDeleteAndPublishEvent);

        log.info("프리랜서 등록 취소 - Self Promotion 삭제 완료.");
    }

    // Self Promotion Soft Delete 및 이벤트 발행
    private void performSoftDeleteAndPublishEvent(SelfPromotionEntity promotion) {
        // 1. Soft Delete 처리
        promotion.delete();
        selfPromotionRepository.save(promotion); // 변경 사항 저장

        // 2. 삭제 이벤트 발행
        SelfPromotionDeletedEvent deletedEvent = new SelfPromotionDeletedEvent(promotion.getCode());
        kafkaProducer.send(selfPromotionTopic, promotion.getCode(), deletedEvent);

        log.info("Soft Deleted SelfPromotion: {}", promotion.getCode());
    }

    // SelfPromotionService.java 내부에 SelfPromotion VO 변환 헬퍼
    private SelfPromotion toSelfPromotionVo(SelfPromotionEntity entity) {
        // 1. 회원 닉네임 조회 (기존 getMemberNickname 재사용)
        String memberNickname = getMemberNickname(entity.getMemberCode());

        return new SelfPromotion(
                entity.getCode(),
                entity.getTitle(),
                entity.getContent(),
                entity.getMemberCode(),
                memberNickname,
                entity.getPaymentType(),
                entity.getUnitAmount(),
                entity.getUpdatedAt()
        );
    }

    // SelfPromotionEntity를 SelfPromotionResponse DTO로 변환
    private SelfPromotionResponse toResponse(SelfPromotionEntity entity) {
        // 1. 회원 닉네임 조회
        String memberNickname = getMemberNickname(entity.getMemberCode());

        // 2. pdf 다운로드 URL 생성
        String downloadUrl = null;
        if (entity.getPdfKey() != null) {
            downloadUrl = s3ResourceService.getPdfDownloadUrl(entity.getPdfKey());
        }

        return new SelfPromotionResponse(
                entity.getCode(),
                entity.getMemberCode(),
                memberNickname,
                entity.getTitle(),
                entity.getContent(),
                entity.getPaymentType(),
                entity.getUnitAmount(),
                entity.getResumeCode(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                downloadUrl
        );
    }
}
