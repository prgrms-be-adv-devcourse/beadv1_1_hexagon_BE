package com.example.profileservice.tag.api;

import com.example.profileservice.tag.model.dto.request.TagRequest;
import com.example.profileservice.tag.model.dto.response.TagResponse;
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
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Tag API", description = "기술 태그 및 회원-태그 연결 관리")
public interface TagApiController {

    // 전체 태그 목록 조회
    @Operation(summary = "전체 태그 목록 조회", description = "시스템에 등록된 모든 기술 태그 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "태그 목록 조회 성공")
    ResponseEntity<ResponseDto<List<TagResponse>>> getAllTags();

    // 새 태그 등록
    @Operation(summary = "새 태그 등록", description = "새로운 기술 태그를 시스템에 등록합니다. (중복 등록 시 에러)")
    @ApiResponse(responseCode = "201", description = "태그 등록 성공")
    @ApiResponse(responseCode = "409", description = "이미 존재하는 태그",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = @ExampleObject(value = "{\"code\": 3502, \"httpStatus\": 409,"
                            + " \"message\": \"이미 존재하는 기술 태그입니다.\", \"data\": null}")
            ))
    @ApiResponse(responseCode = "400", description = "잘못된 입력 값",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = @ExampleObject(value = "{\"code\": 3001, \"httpStatus\": 400,"
                            + " \"message\": \"입력 값이 유효하지 않습니다. (Field: skill)\", \"data\": null}")
            ))
    ResponseEntity<ResponseDto<TagResponse>> createTag(@Valid @RequestBody TagRequest request);

    // 회원 태그 목록 조회
    @Operation(summary = "회원 태그 목록 조회", description = "로그인된 회원이 보유한 기술 태그 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "회원 태그 목록 조회 성공")
    ResponseEntity<ResponseDto<List<TagResponse>>> getMyTags(
            @Parameter(in = ParameterIn.HEADER, required = true, name = "X-CODE", description = "회원 고유 코드")
            @RequestHeader(value = "X-CODE") String memberCode);

    // 회원 태그 연결
    @Operation(summary = "회원 태그 연결", description = "로그인된 회원의 프로필에 특정 태그를 연결합니다. (MemberTagEntity 생성)")
    @ApiResponse(responseCode = "200", description = "태그 연결 성공")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 태그 코드",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = @ExampleObject(value = "{\"code\": 3501, \"httpStatus\": 404,"
                            + " \"message\": \"해당 기술 태그를 찾을 수 없습니다.\", \"data\": null}")
            ))
    @ApiResponse(responseCode = "409", description = "이미 연결된 태그",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = @ExampleObject(value = "{\"code\": 3503, \"httpStatus\": 409,"
                            + " \"message\": \"이미 연결된 기술 태그입니다.\", \"data\": null}")
            ))
    ResponseEntity<ResponseDto<Empty>> linkMemberTag(@PathVariable String tagCode,
            @RequestHeader(value = "X-CODE") String memberCode);

    // 회원 태그 연결 해제
    @Operation(summary = "회원 태그 연결 해제", description = "로그인된 회원의 프로필에서 특정 태그 연결을 해제합니다. (MemberTagEntity 삭제)")
    @ApiResponse(responseCode = "200", description = "태그 연결 해제 성공")
    @ApiResponse(responseCode = "404", description = "연결 정보 없음",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = @ExampleObject(value = "{\"code\": 3504, \"httpStatus\": 404,"
                            + " \"message\": \"해제할 기술 태그 연결을 찾을 수 없습니다.\", \"data\": null}")
            ))
    ResponseEntity<ResponseDto<Empty>> unlinkMemberTag(@PathVariable String tagCode,
            @RequestHeader(value = "X-CODE") String memberCode);

    // 회원 태그 동기화
    @Operation(summary = "회원 태그 목록 동기화", description = "Member 모듈의 요청으로, 회원 프로필의 태그 목록을 요청 List로 일괄 동기화(업데이트)합니다.")
    @ApiResponse(responseCode = "200", description = "태그 목록 동기화 성공")
    ResponseEntity<ResponseDto<Empty>> syncMemberTags(
            @Parameter(in = ParameterIn.HEADER, required = true, name = "X-CODE", description = "회원 고유 코드")
            @RequestHeader(value = "X-CODE") String memberCode,
            @RequestBody List<String> tagCodes);

    // 기술명으로 태그 정보 조회
    @Operation(summary = "기술명으로 태그 정보 조회", description = "기술 태그 이름(skill)을 통해 해당 태그의 코드와 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "태그 정보 조회 성공")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 태그 이름",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseDto.class),
                    examples = @ExampleObject(value = "{\"code\": 3501, \"httpStatus\": 404,"
                            + " \"message\": \"해당 기술 태그를 찾을 수 없습니다.\", \"data\": null}"))
    )
    ResponseEntity<ResponseDto<TagResponse>> getTagBySkill(
            @Parameter(description = "조회할 기술 태그 이름", example = "Java")
            @RequestParam("skill") String skill);

    // 태그 코드 목록으로 태그 정보 일괄 조회
    @Operation(summary = "태그 코드 목록으로 조회", description = "여러 개의 태그 코드를 전달받아 해당 태그들의 정보를 일괄 조회합니다.")
    @ApiResponse(responseCode = "200", description = "태그 목록 조회 성공")
    ResponseEntity<ResponseDto<List<TagResponse>>> getTagsByCodes(@RequestParam("codes") List<String> codes);
}
