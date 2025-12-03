package com.example.profileservice.rating.api;

import com.example.profileservice.rating.model.dto.request.RatingRequest;
import com.example.profileservice.rating.model.dto.response.RatingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Rating API", description = "회원 평가 관리 (카운트 집계)")
public interface RatingApiController {

    // 특정 회원 평가 조회
    @Operation(summary = "특정 회원 평가 조회", description = "특정 회원이 받은 만족/불만족 평가 카운트를 조회합니다. (인증 불필요)")
    @ApiResponse(responseCode = "200", description = "평가 조회 성공 (평가가 없으면 0/0으로 반환)",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = RatingResponse.class),
                    examples = @ExampleObject(name = "Success Response",
                            value = "{\"code\": 0, \"httpStatus\": 200, \"message\": \"요청에 성공했습니다.\", \"data\": {\"memberCode\": \"user-to-be-rated-001\", \"satisfiedCount\": 42, \"unsatisfiedCount\": 3}}")))
    ResponseEntity<ResponseDto<RatingResponse>> getMemberRating(@PathVariable String memberCode);

    // 평가 등록/업데이트
    @Operation(summary = "평가 등록/업데이트", description = "특정 회원에게 만족 또는 불만족 평가 카운트를 1 증가시킵니다. (최초 평가 시 엔티티 자동 생성)")
    @ApiResponse(responseCode = "200", description = "평가 업데이트 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = RatingResponse.class),
                    examples = @ExampleObject(name = "Update Success",
                            value = "{\"code\": 0, \"httpStatus\": 200, \"message\": \"요청에 성공했습니다.\", \"data\": {\"memberCode\": \"user-to-be-rated-001\", \"satisfiedCount\": 43, \"unsatisfiedCount\": 3}}")))
    @ApiResponse(responseCode = "400", description = "자기 자신 평가 시도",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = @ExampleObject(name = "Self Rating Not Allowed",
                            value = "{\"code\": 3202, \"httpStatus\": 400, \"message\": \"자기 자신을 평가할 수 없습니다.\", \"data\": null}")))
    ResponseEntity<ResponseDto<RatingResponse>> updateRating(
            @Parameter(in = ParameterIn.HEADER, required = true, name = "X-CODE", description = "평가하는 회원 고유 코드 (Caller)")
            @RequestHeader(value = "X-CODE") String callerCode,
            @PathVariable String memberCode,
            @Valid @RequestBody RatingRequest request);
}
