package com.example.communicationservice.controller.api;

import com.example.communicationservice.controller.dto.request.ChatFileUploadUrlGenerateRequest;
import com.example.communicationservice.controller.dto.response.ChatFileUploadUrlGenerateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.http.ResponseEntity;

@Tag(name = "ChatFile API", description = "채팅 메시지에 포함된 파일 관리")
public interface ChatFileControllerApi {

    @Operation(
        summary = "파일 업로드 URL 생성",
        description = "특정 채팅방에 파일이 포함된 메시지를 전송하기 위해 파일 업로드 URL을 생성합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "파일 업로드 URL 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    ResponseEntity<ResponseDto<ChatFileUploadUrlGenerateResponse>> generateUploadUrl(
        @Parameter(
            name = "room-id",
            description = "채팅방 아이디",
            required = true,
            in = ParameterIn.PATH,
            example = "651f7c8b9a1d4c6f8b2e3d1a"
        )
        String roomId,

        @Parameter(
            name = "X-CODE",
            description = "현재 로그인한 회원 코드",
            required = true,
            in = ParameterIn.HEADER,
            example = "abc-12345-ABC"
        )
        String senderCode,

        @RequestBody(
            description = "파일 업로드 URL 생성 요청 DTO",
            required = true
        )
        ChatFileUploadUrlGenerateRequest request
    );

}
