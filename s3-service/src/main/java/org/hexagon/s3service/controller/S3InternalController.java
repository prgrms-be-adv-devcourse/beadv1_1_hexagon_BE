package org.hexagon.s3service.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.hexagon.s3service.controller.swagger.S3ControllerSwagger;
import org.hexagon.s3service.controller.swagger.S3InternalControllerSwagger;
import org.hexagon.s3service.dto.ExistsResponse;
import org.hexagon.s3service.dto.PresignedDownloadListResponse;
import org.hexagon.s3service.dto.PresignedDownloadRequestByCode;
import org.hexagon.s3service.dto.PresignedDownloadRequestByKey;
import org.hexagon.s3service.dto.PresignedUploadRequest;
import org.hexagon.s3service.dto.PresignedUploadResponse;
import org.hexagon.core.vo.ServiceName;
import org.hexagon.s3service.dto.StoreKeysRequest;
import org.hexagon.s3service.service.S3Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/s3")
public class S3InternalController implements S3InternalControllerSwagger {

    private final S3Service s3Service;

    @PostMapping("/upload-url")
    public ResponseDto<PresignedUploadResponse> getUploadUrl(@Valid @RequestBody PresignedUploadRequest request) {
        // internal은 CHATS만 허용
        if (request.serviceName() != ServiceName.CHATS) {
            throw new IllegalArgumentException("internal API는 CHATS만 허용됩니다.");
        }
        PresignedUploadResponse response = s3Service.createUploadUrlForChats(
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
        PresignedDownloadListResponse downloadUrls = s3Service.createDownloadUrls(request.code());
        return ResponseDto.success(downloadUrls);
    }

    @PostMapping("/s3-resource")
    public ResponseDto<Empty> storeKeys(@RequestBody StoreKeysRequest request) {
        s3Service.saveResources(request.code(), request.keys());
        return ResponseDto.success(HttpStatus.CREATED);
    }

    @PatchMapping("/s3-resource")
    public ResponseDto<Empty> updateKeys(@RequestBody StoreKeysRequest request) {
        List<String> keys = request.keys();
        if(keys == null) {
            keys = List.of();
        }
        s3Service.syncAttachments(request.code(), keys);
        return ResponseDto.success(HttpStatus.CREATED);
    }

    @GetMapping("/exists")
    public ResponseDto<ExistsResponse> exists(@RequestParam String key) {
        ExistsResponse response = s3Service.exists(key);
        return ResponseDto.success(response);
    }
}
