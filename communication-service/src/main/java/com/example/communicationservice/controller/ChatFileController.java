package com.example.communicationservice.controller;

import com.example.communicationservice.controller.dto.request.ChatFileUploadUrlGenerateRequest;
import com.example.communicationservice.controller.dto.response.ChatFileUploadUrlGenerateResponse;
import com.example.communicationservice.service.ChatFileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatrooms/{room-id}/files")
public class ChatFileController {

    private final ChatFileService chatFileService;

    // 파일 업로드 URL 생성 API
    @PostMapping("/upload-url")
    public ResponseEntity<ResponseDto<ChatFileUploadUrlGenerateResponse>> generateUploadUrl(
        @PathVariable(name = "room-id") String roomId,
        @RequestHeader(name = "X-CODE") String senderCode,
        @Valid @RequestBody ChatFileUploadUrlGenerateRequest request
    ) {
        ChatFileUploadUrlGenerateResponse response = chatFileService.generateUploadUrl(roomId, senderCode, request);

        return ResponseEntity
            .ok()
            .body(ResponseDto.success(response));
    }

}
