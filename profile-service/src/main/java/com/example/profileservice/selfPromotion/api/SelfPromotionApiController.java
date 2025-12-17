package com.example.profileservice.selfPromotion.api;

import com.example.profileservice.selfPromotion.model.dto.request.SelfPromotionCreateRequest;
import com.example.profileservice.selfPromotion.model.dto.request.SelfPromotionUpdateRequest;
import com.example.profileservice.selfPromotion.model.dto.response.SelfPromotionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.hexagon.core.dto.ResponseDto;
import org.hexagon.core.dto.Empty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Self Promotion API", description = "프리랜서 수주글 관리")
public interface SelfPromotionApiController {

    // 전체 프로모션 목록 조회
    @Operation(summary = "전체 프로모션 목록 조회", description = "시스템에 등록된 모든 활성 프로모션 게시글 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "목록 조회 성공")
    ResponseEntity<ResponseDto<List<SelfPromotionResponse>>> getAllPromotions();

    // 내 프로모션 조회
    @Operation(summary = "내 프로모션 조회", description = "요청 회원이 작성한 활성 프로모션 단건을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "200", description = "활성 프로모션이 없는 경우 'data' 필드는 null",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = @ExampleObject(name = "Not Found", value = "{\"code\": 0, \"httpStatus\": 200, \"message\": \"요청 성공\", \"data\": null}")))
    ResponseEntity<ResponseDto<SelfPromotionResponse>> getMyPromotions(
            @Parameter(in = ParameterIn.HEADER, required = true, name = "X-CODE", description = "회원 고유 코드")
            @RequestHeader(value = "X-CODE") String memberCode);

    // 프로모션 등록
    @Operation(summary = "프로모션 등록", description = "새로운 셀프 프로모션 게시글을 등록합니다. (멤버당 1건만 활성 가능)")
    @ApiResponse(responseCode = "201", description = "등록 성공")
    @ApiResponse(responseCode = "409", description = "이미 활성 프로모션이 존재함",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = @ExampleObject(name = "Promotion Already Exists",
                            value = "{\"code\": 3404, \"httpStatus\": 409, \"message\": \"이미 활성 상태의 셀프 프로모션 게시글이 존재합니다.\", \"data\": null}")))
    ResponseEntity<ResponseDto<SelfPromotionResponse>> createPromotion(
            @Parameter(in = ParameterIn.HEADER, required = true, name = "X-CODE", description = "회원 고유 코드")
            @RequestHeader(value = "X-CODE") String memberCode,
            @Valid @RequestBody SelfPromotionCreateRequest request);

    // 프로모션 상세 조회
    @Operation(summary = "프로모션 상세 조회", description = "특정 프로모션 게시글의 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "상세 조회 성공")
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = @ExampleObject(name = "Promotion Not Found",
                            value = "{\"code\": 3401, \"httpStatus\": 404, \"message\": \"요청하신 셀프 프로모션 게시글을 찾을 수 없습니다.\", \"data\": null}")))
    ResponseEntity<ResponseDto<SelfPromotionResponse>> getPromotionDetail(@PathVariable String promotionCode);

    // 프로모션 수정
    @Operation(summary = "프로모션 수정", description = "특정 프로모션 게시글의 내용을 수정합니다. (작성자만 가능)")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @ApiResponse(responseCode = "403", description = "접근 권한 없음",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = @ExampleObject(name = "Unauthorized Access",
                            value = "{\"code\": 3402, \"httpStatus\": 403, \"message\": \"해당 셀프 프로모션 게시글에 대한 접근 권한이 없습니다.\", \"data\": null}")))
    ResponseEntity<ResponseDto<SelfPromotionResponse>> updatePromotion(
            @Parameter(in = ParameterIn.HEADER, required = true, name = "X-CODE", description = "회원 고유 코드")
            @RequestHeader(value = "X-CODE") String memberCode,
            @PathVariable String promotionCode,
            @Valid @RequestBody SelfPromotionUpdateRequest request);

    // 프로모션 삭제
    @Operation(summary = "프로모션 삭제", description = "특정 프로모션 게시글을 논리적으로 삭제합니다. (작성자만 가능)")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @ApiResponse(responseCode = "403", description = "접근 권한 없음",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = @ExampleObject(name = "Unauthorized Access",
                            value = "{\"code\": 3402, \"httpStatus\": 403, \"message\": \"해당 셀프 프로모션 게시글에 대한 접근 권한이 없습니다.\", \"data\": null}")))
    ResponseEntity<ResponseDto<Empty>> deletePromotion(
            @Parameter(in = ParameterIn.HEADER, required = true, name = "X-CODE", description = "회원 고유 코드")
            @RequestHeader(value = "X-CODE") String memberCode,
            @PathVariable String promotionCode);
}
