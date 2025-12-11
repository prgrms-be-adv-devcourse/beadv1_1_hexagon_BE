package com.example.profileservice.selfPromotion.api;

import com.example.profileservice.selfPromotion.model.dto.request.SelfPromotionCreateRequest;
import com.example.profileservice.selfPromotion.model.dto.request.SelfPromotionUpdateRequest;
import com.example.profileservice.selfPromotion.model.dto.response.SelfPromotionResponse;
import com.example.profileservice.selfPromotion.service.SelfPromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/self-promotions")
@RequiredArgsConstructor
public class SelfPromotionController implements SelfPromotionApiController {

    // Gateway 환경이 구축되지 않았을 때를 위한 임시 기본값
    private static final String DEFAULT_MEMBER_CODE = "member-uuid-code-001";

    private final SelfPromotionService selfPromotionService;

    // 전체 프로모션 목록 조회
    @Override
    @GetMapping
    public ResponseEntity<ResponseDto<List<SelfPromotionResponse>>> getAllPromotions() {
        List<SelfPromotionResponse> promotions = selfPromotionService.getAllPromotions();

        return ResponseEntity.ok(ResponseDto.success(promotions));
    }


    // 내 프로모션 목록 조회
    @Override
    @GetMapping("/me")
    public ResponseEntity<ResponseDto<List<SelfPromotionResponse>>> getMyPromotions(
            @RequestHeader(value = "X-CODE", defaultValue = DEFAULT_MEMBER_CODE) String memberCode
    ) {
        List<SelfPromotionResponse> promotions = selfPromotionService.getMyPromotions(memberCode);

        return ResponseEntity.ok(ResponseDto.success(promotions));
    }

    // 프로모션 등록
    @Override
    @PostMapping
    public ResponseEntity<ResponseDto<SelfPromotionResponse>> createPromotion(
            @RequestHeader(value = "X-CODE", defaultValue = DEFAULT_MEMBER_CODE) String memberCode,
            @Valid @RequestBody SelfPromotionCreateRequest request) {

        SelfPromotionResponse newPromotion = selfPromotionService.createPromotion(memberCode, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.success(newPromotion));
    }

    // 프로모션 상세 조회
    @Override
    @GetMapping("/{promotionCode}")
    public ResponseEntity<ResponseDto<SelfPromotionResponse>> getPromotionDetail(
            @PathVariable String promotionCode) {
        SelfPromotionResponse promotion = selfPromotionService.getPromotionDetail(promotionCode);

        return ResponseEntity.ok(ResponseDto.success(promotion));
    }

    // 프로모션 수정
    @Override
    @PatchMapping("/{promotionCode}")
    public ResponseEntity<ResponseDto<SelfPromotionResponse>> updatePromotion(
            @RequestHeader(value = "X-CODE", defaultValue = DEFAULT_MEMBER_CODE) String memberCode,
            @PathVariable String promotionCode,
            @Valid @RequestBody SelfPromotionUpdateRequest request) {
        SelfPromotionResponse updatedPromotion = selfPromotionService.updatePromotion(memberCode, promotionCode, request);

        return ResponseEntity.ok(ResponseDto.success(updatedPromotion));
    }

    // 프로모션 삭제
    @Override
    @DeleteMapping("/{promotionCode}")
    public ResponseEntity<ResponseDto<Empty>> deletePromotion(
            @RequestHeader(value = "X-CODE", defaultValue = DEFAULT_MEMBER_CODE) String memberCode,
            @PathVariable String promotionCode) {
        selfPromotionService.deletePromotion(memberCode, promotionCode);

        return ResponseEntity.ok(ResponseDto.success(Empty.getInstance()));
    }

    // 멤버 모듈에서 호출할 셀프 프로모션 일괄 삭제 API
    @DeleteMapping("/internal/member/{memberCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ResponseDto<Empty>> deletePromotionsForUnregister(
            @PathVariable String memberCode) {

        // 해당 회원의 Self Promotion 정보만 Soft Delete 처리합니다.
        selfPromotionService.deletePromotionsByMemberCode(memberCode);

        return ResponseEntity.ok(ResponseDto.success(Empty.getInstance()));
    }
}
