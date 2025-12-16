package com.example.profileservice.rating.service;

import static com.example.profileservice.common.model.vo.ErrorCode.CANNOT_RATE_MYSELF;
import static com.example.profileservice.common.model.vo.ErrorCode.CONTRACT_FETCH_FAILED;
import static com.example.profileservice.common.model.vo.ErrorCode.CONTRACT_NOT_COMPLETED;
import static com.example.profileservice.common.model.vo.ErrorCode.CONTRACT_NOT_FOUND;
import static com.example.profileservice.common.model.vo.ErrorCode.RATING_ALREADY_SUBMITTED;
import static com.example.profileservice.common.model.vo.ErrorCode.RATING_MEMBER_NOT_FOUND;
import static com.example.profileservice.common.model.vo.ErrorCode.UNAUTHORIZED_RATING_ACCESS;

import com.example.profileservice.common.model.vo.ErrorCode;
import com.example.profileservice.common.model.vo.exception.CustomException;
import com.example.profileservice.common.model.vo.util.ContractFeignClient;
import com.example.profileservice.common.model.vo.util.ContractInfo;
import com.example.profileservice.common.model.vo.util.ContractStatus;
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
    private final ContractFeignClient contractFeignClient;

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

        // 2. 계약 유효성 및 권한 검증
        ContractInfo contractInfo = validateContractAndPermission(
                request.contractCode(), callerCode, receiverCode);

        // 3. 중복 평가 방지 확인
        if (checkDuplicateRating(request.contractCode(), callerCode)) {
            throw new CustomException(RATING_ALREADY_SUBMITTED);
        }

        // 4. Member 모듈을 통해 callerCode와 receiverCode 모두 유효한 회원인지 검증
        List<String> codesToValidate = List.of(callerCode, receiverCode);
        ResponseDto<MemberExistOutput> response = memberFeignClient.existMemberByCode(codesToValidate);

        if (response.data() == null || (response.data().notExists() != null && !response.data().notExists().isEmpty())) {
            throw new CustomException(ErrorCode.INVALID_MEMBER_CODE);
        }

        // 5. 평가 대상 엔티티 확인 (없으면 생성)
        if (!ratingRepository.existsByReceiverCode(receiverCode)) {
            RatingEntity newRating = RatingEntity.builder().receiverCode(receiverCode).build();
            ratingRepository.saveAndFlush(newRating);
        }

        entityManager.flush();

        // 6. 원자적 업데이트 쿼리 실행
        if (request.satisfied()) {
            ratingRepository.incrementSatisfiedCount(receiverCode);
        } else {
            ratingRepository.incrementUnsatisfiedCount(receiverCode);
        }

        entityManager.clear();

        // 7. 업데이트된 최신 데이터 조회 후 반환
        RatingEntity updatedRating = ratingRepository.findByReceiverCode(receiverCode)
                .orElseThrow(() -> new CustomException(RATING_MEMBER_NOT_FOUND));

        return toResponse(updatedRating);
    }

    // 계약 코드 유효성 검증, 계약 상태 검증, 평가 권한 검증을 수행
    private ContractInfo validateContractAndPermission(String contractCode, String callerCode, String receiverCode) {
        ResponseDto<ContractInfo> contractResponse;
        try {
            contractResponse = contractFeignClient.getContractByCode(contractCode);
        } catch (Exception e) {
            log.error("Failed to fetch contract info for code: {}", contractCode, e);
            throw new CustomException(CONTRACT_FETCH_FAILED);
        }

        ContractInfo contract = contractResponse.data();

        // 1) 계약 존재 여부 확인
        if (contract == null) {
            throw new CustomException(CONTRACT_NOT_FOUND);
        }

        // 2) 계약 상태 확인: DONE 상태만 평가 가능
        if (contract.status() != ContractStatus.DONE) {
            throw new CustomException(CONTRACT_NOT_COMPLETED);
        }

        // 3) 당사자 및 권한 확인
        boolean isCallerClient = contract.clientCode().equals(callerCode) && contract.freelancerCode().equals(receiverCode);
        boolean isCallerFreelancer = contract.freelancerCode().equals(callerCode) && contract.clientCode().equals(receiverCode);

        if (!(isCallerClient || isCallerFreelancer)) {
            // 평가하는 사람이 계약 당사자 중 한 명이고, 받는 사람이 상대방이어야 함
            throw new CustomException(UNAUTHORIZED_RATING_ACCESS);
        }

        return contract;
    }

    // 계약 모듈을 통해 해당 계약에 대해 이미 평가했는지 확인
    private boolean checkDuplicateRating(String contractCode, String callerCode) {
        ResponseDto<Boolean> ratedResponse;
        try {
            ratedResponse = contractFeignClient.isContractRatedBy(contractCode, callerCode);
        } catch (Exception e) {
            log.error("Failed to check duplicate rating for contract: {}", contractCode, e);
            // 통신 오류는 중복 평가로 간주하여 방지
            return true;
        }

        return ratedResponse.data() != null && ratedResponse.data();
    }

    private RatingResponse toResponse(RatingEntity entity) {
        return new RatingResponse(
                entity.getReceiverCode(),
                entity.getSatisfiedCount(),
                entity.getUnsatisfiedCount()
        );
    }
}
