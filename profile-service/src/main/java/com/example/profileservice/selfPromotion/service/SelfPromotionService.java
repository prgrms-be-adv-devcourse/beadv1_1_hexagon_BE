package com.example.profileservice.selfPromotion.service;

import static org.apache.kafka.common.requests.FetchMetadata.log;

import com.example.profileservice.common.model.vo.ErrorCode;
import com.example.profileservice.common.model.vo.KafkaProducer;
import com.example.profileservice.common.model.vo.ResponseDto;
import com.example.profileservice.common.model.vo.exception.CustomException;
import com.example.profileservice.common.model.vo.util.MemberFeignClient;
import com.example.profileservice.resume.repository.ResumeRepository;
import com.example.profileservice.selfPromotion.model.dto.request.SelfPromotionCreateRequest;
import com.example.profileservice.selfPromotion.model.dto.request.SelfPromotionEvent;
import com.example.profileservice.selfPromotion.model.dto.request.SelfPromotionUpdateRequest;
import com.example.profileservice.selfPromotion.model.dto.response.SelfPromotionEsEventData;
import com.example.profileservice.selfPromotion.model.dto.response.SelfPromotionResponse;
import com.example.profileservice.selfPromotion.model.entity.SelfPromotionEntity;
import com.example.profileservice.selfPromotion.repository.SelfPromotionRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SelfPromotionService {

    private final SelfPromotionRepository selfPromotionRepository;
    private final ResumeRepository resumeRepository;
    private final KafkaProducer kafkaProducer;
    private final MemberFeignClient memberFeignClient;

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

    // 특정 회원이 작성한 셀프 프로모션 게시글 목록을 최신순으로 조회
    public List<SelfPromotionResponse> getMyPromotions(String memberCode) {
        List<SelfPromotionEntity> myPromotions = selfPromotionRepository.findAllByMemberCodeAndIsDeletedFalseOrderByCreatedAtDesc(memberCode);

        return myPromotions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
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
        // 1. 이력서 유효성 검증
        validateResumeCode(request.resumeCode());

        // 2. SelfPromotion 엔티티 생성 및 저장
        SelfPromotionEntity promotion = SelfPromotionEntity.create(
                memberCode,
                request.title(),
                request.content(),
                request.paymentType(),
                request.unitAmount(),
                request.resumeCode()
        );

        selfPromotionRepository.save(promotion);

        SelfPromotionResponse response = toResponse(promotion);

        // 3. 이벤트 발행 (CREATE)
        kafkaProducer.send(selfPromotionTopic,
                SelfPromotionEvent.create(SelfPromotionEsEventData.fromResponse(response)));

        return toResponse(promotion);
    }

    // 특정 셀프 프로모션 게시글을 수정
    @Transactional
    public SelfPromotionResponse updatePromotion(String memberCode, String promotionCode, SelfPromotionUpdateRequest request) {
        // 1. 프로모션 존재 및 권한 확인
        SelfPromotionEntity promotion = getPromotionOrThrow(promotionCode, memberCode);

        // 2. 이력서 유효성 검증
        validateResumeCode(request.resumeCode());

        // 3. 수정 (요청에 포함된 필드만 업데이트하며, null이 올 경우 기존 값 유지)
        promotion.update(
                Optional.ofNullable(request.title()).orElse(promotion.getTitle()),
                Optional.ofNullable(request.content()).orElse(promotion.getContent()),
                Optional.ofNullable(request.paymentType()).orElse(promotion.getPaymentType()),
                Optional.ofNullable(request.unitAmount()).orElse(promotion.getUnitAmount()),
                request.resumeCode()
        );

        SelfPromotionResponse response = toResponse(promotion);

        // 4. 이벤트 발행 (UPDATE)
        kafkaProducer.send(selfPromotionTopic,
                SelfPromotionEvent.update(SelfPromotionEsEventData.fromResponse(response)));

        return toResponse(promotion);
    }

    // 특정 셀프 프로모션 게시글을 삭제 처리
    @Transactional
    public void deletePromotion(String memberCode, String promotionCode) {
        // 1. 프로모션 존재 및 권한 확인
        SelfPromotionEntity promotion = getPromotionOrThrow(promotionCode, memberCode);

        // 2. Soft Delete 처리
        promotion.delete();

        SelfPromotionResponse response = toResponse(promotion);

        // 3. 이벤트 발행 (DELETE)
        kafkaProducer.send(selfPromotionTopic,
                SelfPromotionEvent.delete(promotionCode));
    }

    // 회원 코드로 닉네임을 조회하는 헬퍼 메서드
    private String getMemberNickname(String memberCode) {
        try {
            ResponseDto<MemberInfoOutput> response = memberFeignClient.getMemberInfoByCode(List.of(memberCode));

            if (response.getData() == null || response.getData().internalMemberInfos().isEmpty()) {
                // 회원 정보는 존재하지만(컨트롤러에서 예외 처리) 빈 목록일 경우
                log.warn("Member info not found for code: {}", memberCode);
                return "알 수 없음";
            }

            return response.getData().internalMemberInfos().get(0).nickName();

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

    // SelfPromotionEntity를 SelfPromotionResponse DTO로 변환
    private SelfPromotionResponse toResponse(SelfPromotionEntity entity) {
        // 1. 회원 닉네임 조회
        String memberNickname = getMemberNickname(entity.getMemberCode());

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
                entity.getUpdatedAt()
        );
    }
}
