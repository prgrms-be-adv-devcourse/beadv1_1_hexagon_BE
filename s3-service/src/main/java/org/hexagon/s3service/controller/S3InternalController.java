package org.hexagon.s3service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.dto.ResponseDto;
import org.hexagon.s3service.dto.PresignedUploadRequest;
import org.hexagon.s3service.dto.PresignedUploadResponse;
import org.hexagon.s3service.dto.ServiceName;
import org.hexagon.s3service.service.S3Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/s3")
public class S3InternalController {

    private final S3Service s3Service;

    @PostMapping("/upload-url")
    public ResponseDto<PresignedUploadResponse> getUploadUrl(@Valid @RequestBody PresignedUploadRequest request) {
        // internal은 CHATS만 허용
        if (request.serviceName() != ServiceName.CHATS) {
            throw new IllegalArgumentException("internal API는 CHATS만 허용됩니다.");
        }
        PresignedUploadResponse response = s3Service.createUploadUrl(
                request.serviceName(),
                request.fileName(),
                request.contentType()
        );
        return ResponseDto.success(response);
    }
}
