package com.example.profileservice.rating.service;

import static com.example.profileservice.common.model.vo.ErrorCode.CANNOT_RATE_MYSELF;
import static com.example.profileservice.common.model.vo.ErrorCode.CONTRACT_NOT_COMPLETED;
import static com.example.profileservice.common.model.vo.ErrorCode.RATING_MEMBER_NOT_FOUND;

import com.example.profileservice.common.model.vo.ErrorCode;
import com.example.profileservice.common.model.vo.exception.CustomException;
import com.example.profileservice.common.model.vo.util.CompletedContractStore;
import com.example.profileservice.common.model.vo.util.MemberExistOutput;
import com.example.profileservice.common.model.vo.util.MemberFeignClient;
import com.example.profileservice.rating.model.dto.request.RatingRequest;
import com.example.profileservice.rating.model.dto.response.RatingResponse;
import com.example.profileservice.rating.model.entity.RatingEntity;
import com.example.profileservice.rating.repository.RatingRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RatingService {

    private final RatingRepository ratingRepository;
    private final EntityManager entityManager;
    private final MemberFeignClient memberFeignClient;
    private final CompletedContractStore completedContractStore;

    // 특정 회원의 평가 카운트를 조회
    public RatingResponse getMemberRating(String receiverCode) {
        RatingEntity rating = ratingRepository.findByReceiverCode(receiverCode)
                .orElseGet(() -> RatingEntity.builder().receiverCode(receiverCode).build()); // 존재하지 않으면 0/0으로 임시 생성

        return toResponse(rating);
    }

    // 특정 회원에게 만족 또는 불만족 평가 카운트를 1 증가
    @Transactional
    public RatingResponse updateRating(String callerCode, String receiverCode, RatingRequest request) {

        // 1. 자기 자신 평가 금지
        if (callerCode.equals(receiverCode)) {
            throw new CustomException(CANNOT_RATE_MYSELF);
        }

        // 2. DONE 계약인지 확인 (Kafka 기준)
        if (!completedContractStore.isCompleted(request.contractCode())) {
            throw new CustomException(CONTRACT_NOT_COMPLETED);
        }

        // 3. 회원 존재 검증 (기존 로직 유지)
        List<String> codesToValidate = List.of(callerCode, receiverCode);
        ResponseDto<MemberExistOutput> response =
                memberFeignClient.existMemberByCode(codesToValidate);

        if (response.data() == null ||
                (response.data().notExists() != null && !response.data().notExists().isEmpty())) {
            throw new CustomException(ErrorCode.INVALID_MEMBER_CODE);
        }

        // 4. 평가 대상 엔티티 없으면 생성
        if (!ratingRepository.existsByReceiverCode(receiverCode)) {
            ratingRepository.saveAndFlush(
                    RatingEntity.builder()
                            .receiverCode(receiverCode)
                            .build()
            );
        }

        entityManager.flush();

        // 5. 카운트 증가 (원자적)
        if (Boolean.TRUE.equals(request.satisfied())) {
            ratingRepository.incrementSatisfiedCount(receiverCode);
        } else {
            ratingRepository.incrementUnsatisfiedCount(receiverCode);
        }

        entityManager.clear();

        // 6. 결과 반환
        RatingEntity updatedRating = ratingRepository.findByReceiverCode(receiverCode)
                .orElseThrow(() -> new CustomException(RATING_MEMBER_NOT_FOUND));

        return toResponse(updatedRating);
    }

    private RatingResponse toResponse(RatingEntity entity) {
        return new RatingResponse(
                entity.getReceiverCode(),
                entity.getSatisfiedCount(),
                entity.getUnsatisfiedCount()
        );
    }
}
