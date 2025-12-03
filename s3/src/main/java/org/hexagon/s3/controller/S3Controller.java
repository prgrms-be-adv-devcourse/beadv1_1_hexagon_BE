package org.hexagon.s3.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.hexagon.s3.dto.PresignedDownloadListRequest;
import org.hexagon.s3.dto.PresignedDownloadListResponse;
import org.hexagon.s3.dto.PresignedDownloadRequest;
import org.hexagon.s3.dto.PresignedDownloadResponse;
import org.hexagon.s3.dto.PresignedUploadRequest;
import org.hexagon.s3.dto.PresignedUploadResponse;
import org.hexagon.s3.service.S3Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3")
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/upload-url")
    public ResponseDto<PresignedUploadResponse> getUploadUrl(@RequestBody PresignedUploadRequest request) {
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

    @DeleteMapping
    public ResponseDto<Empty> deleteObject(@RequestParam String key) {
        s3Service.deleteObject(key);
        return ResponseDto.success();
    }
}
