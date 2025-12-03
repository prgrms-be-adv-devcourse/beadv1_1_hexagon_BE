package org.hexagon.s3service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.dto.ResponseDto;
import org.hexagon.s3service.dto.PresignedDownloadListRequest;
import org.hexagon.s3service.dto.PresignedDownloadListResponse;
import org.hexagon.s3service.dto.PresignedDownloadRequest;
import org.hexagon.s3service.dto.PresignedDownloadResponse;
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
@RequestMapping("/api/s3")
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/upload-url")
    public ResponseDto<PresignedUploadResponse> getUploadUrl(@Valid @RequestBody PresignedUploadRequest request) {
        // CHATS는 외부 API 사용 금지
        if (request.serviceName() == ServiceName.CHATS) {
            throw new IllegalArgumentException("CHATS는 외부 API를 사용할 수 없습니다.");
        }
        PresignedUploadResponse response = s3Service.createUploadUrl(
                request.serviceName(),
                request.fileName(),
                request.contentType()
        );
        return ResponseDto.success(response);
    }

    @PostMapping("/download-url")
    public ResponseDto<PresignedDownloadResponse> getDownloadUrl(@RequestBody PresignedDownloadRequest request) {
        PresignedDownloadResponse downloadUrl = s3Service.createDownloadUrl(request.key());
        return ResponseDto.success(downloadUrl);
    }

    @PostMapping("/download-urls")
    public ResponseDto<PresignedDownloadListResponse> getDownloadUrls(@RequestBody PresignedDownloadListRequest request) {
        PresignedDownloadListResponse downloadUrls = s3Service.createDownloadUrls(request.keys());
        return ResponseDto.success(downloadUrls);
    }
}
