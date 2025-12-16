package com.example.profileservice.common.model.vo.util;

import com.example.profileservice.common.model.vo.ErrorCode;
import com.example.profileservice.common.model.vo.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hexagon.core.dto.ResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ResourceService {

    private final S3FeignClient s3FeignClient;

    // S3 베이스 URL (getPdfDownloadUrl 헬퍼에서 사용)
    @Value("${cloud.aws.s3.base-url}")
    private String s3BaseUrl;

    // S3 Resource Code를 사용하여 S3 모듈에 영구 저장 요청 (Create 시)
    public void storeS3Keys(String pdfCode, List<String> keys) {
        StoreKeysRequest request = new StoreKeysRequest(pdfCode, keys);

        try {
            s3FeignClient.storeKeys(request);
        } catch (Exception e) {
            log.error("Failed to store S3 keys for code: {}", pdfCode, e);
            throw new CustomException(ErrorCode.S3_RESOURCE_SAVE_FAILED);
        }
    }

    // S3 Resource Code를 사용하여 S3 모듈에 동기화 요청 (Update/Delete 시)
    public void syncS3Keys(String pdfCode, List<String> keys) {
        StoreKeysRequest request = new StoreKeysRequest(pdfCode, keys);

        try {
            s3FeignClient.updateKeys(request);
        } catch (Exception e) {
            log.error("Failed to sync S3 keys for code: {}", pdfCode, e);
            throw new CustomException(ErrorCode.S3_RESOURCE_SYNC_FAILED);
        }
    }

    // PDF 다운로드 Presigned URL 생성
    public String getPdfDownloadUrl(String pdfCode) {
        try {
            PresignedDownloadRequestByCode request = new PresignedDownloadRequestByCode(pdfCode);

            ResponseDto<PresignedDownloadListResponse> responseDto = s3FeignClient.getDownloadUrlByCode(request);

            if (responseDto.data() != null && responseDto.data().urls() != null && !responseDto.data().urls().isEmpty()) {
                // 단일 pdf 파일을 가정하고 리스트의 첫 번째 요소를 사용
                PresignedDownloadResponse downloadResponse = responseDto.data().urls().get(0);

                // S3 Service가 key + queryString만 반환하므로, S3 버킷의 URL 베이스를 사용
                return s3BaseUrl + downloadResponse.key() + downloadResponse.queryString();
            }
            log.warn("Failed to get download URL for code: {}", pdfCode);
            return null;

        } catch (Exception e) {
            log.error("Error calling S3 Feign Client for download URL: {}", pdfCode, e);
            // 조회 실패 시 null을 반환하도록 기존 로직 유지
            return null;
        }
    }
}
