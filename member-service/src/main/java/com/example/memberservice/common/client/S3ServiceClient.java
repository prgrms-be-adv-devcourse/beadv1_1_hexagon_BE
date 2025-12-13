package com.example.memberservice.common.client;


import com.example.memberservice.common.client.dto.request.s3.PresignedDownloadRequestByCode;
import com.example.memberservice.common.client.dto.request.s3.StoreKeysRequest;
import com.example.memberservice.common.client.dto.response.s3.PresignedDownloadListResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.hexagon.core.dto.Empty;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
    name = "s3-service",
    path = "/internal/s3"
)
public interface S3ServiceClient {

    /**
     * code 기준 다운로드 URL 조회
     */
    @PostMapping("/download-url/code")
    ResponseDto<PresignedDownloadListResponse> getDownloadUrlByCode(
        @RequestBody PresignedDownloadRequestByCode request
    );

    /**
     * S3 리소스 저장
     */
    @PostMapping("/s3-resource")
    ResponseDto<Empty> storeKeys(
        @RequestBody StoreKeysRequest request
    );

    /**
     * S3 리소스 동기화
     */
    @PatchMapping("/s3-resource")
    ResponseDto<Empty> updateKeys(
        @RequestBody StoreKeysRequest request
    );
}
