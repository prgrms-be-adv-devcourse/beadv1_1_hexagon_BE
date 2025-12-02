package org.hexagon.s3.controller;

import lombok.RequiredArgsConstructor;
import org.hexagon.s3.dto.PresignedDownloadRequest;
import org.hexagon.s3.dto.PresignedDownloadResponse;
import org.hexagon.s3.dto.PresignedUploadRequest;
import org.hexagon.s3.dto.PresignedUploadResponse;
import org.hexagon.s3.service.S3Service;
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
    public PresignedUploadResponse getUploadUrl(@RequestBody PresignedUploadRequest request) {
        return s3Service.createUploadUrl(request.serviceName(), request.fileName(), request.contentType());
    }

    @PostMapping("/download-url")
    public PresignedDownloadResponse getDownloadUrl(@RequestBody PresignedDownloadRequest request) {
        return s3Service.createDownloadUrl(request.key());
    }
}
