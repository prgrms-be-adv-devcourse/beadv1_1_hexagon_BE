package com.example.profileservice.common.model.vo.util;

import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "S3-SERVICE")
public interface S3FeignClient {

    // 1. S3 Internal Controller의 Store Keys API 호출 (Create 시 사용)
    @PostMapping("/internal/s3/s3-resource")
    ResponseDto<Empty> storeKeys(@RequestBody StoreKeysRequest request);

    // 2. S3 Internal Controller의 Update Keys API 호출 (Update 시 사용)
    @PatchMapping("/internal/s3/s3-resource")
    ResponseDto<Empty> updateKeys(@RequestBody StoreKeysRequest request);

    // 3. S3 Internal Controller의 Download URL by Code API 호출 (Detail 조회 시 사용)
    @PostMapping("/internal/s3/download-url/code")
    ResponseDto<PresignedDownloadListResponse> getDownloadUrlByCode(
            @RequestBody PresignedDownloadRequestByCode request);
}
