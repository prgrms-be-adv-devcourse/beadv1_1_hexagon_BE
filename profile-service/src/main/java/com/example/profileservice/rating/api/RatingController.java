package com.example.profileservice.rating.api;

import com.example.profileservice.rating.model.dto.request.RatingRequest;
import com.example.profileservice.rating.model.dto.response.RatingResponse;
import com.example.profileservice.rating.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController implements RatingApiController {

    // Gateway 환경이 구축되지 않았을 때를 위한 임시 기본값
    private static final String DEFAULT_MEMBER_CODE = "member-uuid-code-001";

    private final RatingService ratingService;

    @Override
    @GetMapping("/{memberCode}")
    public ResponseEntity<ResponseDto<RatingResponse>> getMemberRating(@PathVariable String memberCode) {
        RatingResponse response = ratingService.getMemberRating(memberCode);

        return ResponseEntity.ok(ResponseDto.success(response));
    }

    @Override
    @PatchMapping("/{memberCode}")
    public ResponseEntity<ResponseDto<RatingResponse>> updateRating(
            @RequestHeader(value = "X-CODE", defaultValue = DEFAULT_MEMBER_CODE) String callerCode, // X-CODE를 callerCode로 사용
            @PathVariable String memberCode, // PathVariable을 receiverCode로 사용
            @Valid @RequestBody RatingRequest request) {

        RatingResponse response = ratingService.updateRating(callerCode, memberCode, request);

        return ResponseEntity.ok(ResponseDto.success(response));
    }
}
