package org.hexagon.s3service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.hexagon.s3service.dto.PresignedDownloadListResponse;
import org.hexagon.s3service.dto.PresignedDownloadRequestByCode;
import org.hexagon.s3service.dto.PresignedDownloadRequestByKey;
import org.hexagon.s3service.dto.PresignedUploadRequest;
import org.hexagon.s3service.dto.PresignedUploadResponse;
import org.hexagon.core.vo.ServiceName;
import org.hexagon.s3service.dto.StoreKeysRequest;
import org.hexagon.s3service.service.S3Service;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/download-url/key")
    public ResponseDto<PresignedDownloadListResponse> getDownloadUrl(@RequestBody PresignedDownloadRequestByKey request) {
        PresignedDownloadListResponse downloadUrls = s3Service.createDownloadUrls(request.keys());
        return ResponseDto.success(downloadUrls);
    }

    @PostMapping("/download-url/code")
    public ResponseDto<PresignedDownloadListResponse> getDownloadUrl(@RequestBody PresignedDownloadRequestByCode request) {
        PresignedDownloadListResponse downloadUrls = new PresignedDownloadListResponse(null);
        return ResponseDto.success(downloadUrls);
    }

    @PostMapping("/s3-resource")
    public ResponseDto<Empty> storeKeys(@RequestBody StoreKeysRequest request) {
        return ResponseDto.success(HttpStatus.CREATED);
    }
}
