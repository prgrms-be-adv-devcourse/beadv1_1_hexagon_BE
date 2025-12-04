package org.hexagon.s3service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.hexagon.s3service.dto.PresignedUploadRequest;
import org.hexagon.s3service.dto.PresignedUploadResponse;
import org.hexagon.s3service.vo.ServiceName;
import org.hexagon.s3service.service.S3Service;
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

    // TODO: 모든 API 구현 완료 후 주석 삭제
//    @PostMapping("/download-url")
//    public ResponseDto<PresignedDownloadResponse> getDownloadUrl(@RequestBody PresignedDownloadRequest request) {
//        PresignedDownloadResponse downloadUrl = s3Service.createDownloadUrl(request.key());
//        return ResponseDto.success(downloadUrl);
//    }

//    @PostMapping("/download-urls")
//    public ResponseDto<PresignedDownloadListResponse> getDownloadUrls(@RequestBody PresignedDownloadRequestByKey request) {
//        PresignedDownloadListResponse downloadUrls = s3Service.createDownloadUrls(request.keys());
//        return ResponseDto.success(downloadUrls);
//    }

    @DeleteMapping
    public ResponseDto<Empty> deleteObject(@RequestParam String key) {
        s3Service.deleteObject(key);
        return ResponseDto.success();
    }
}
