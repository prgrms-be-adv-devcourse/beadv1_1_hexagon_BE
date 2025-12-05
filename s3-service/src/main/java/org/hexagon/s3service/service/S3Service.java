package org.hexagon.s3service.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.hexagon.s3service.dto.PresignedDownloadListResponse;
import org.hexagon.s3service.dto.PresignedDownloadResponse;
import org.hexagon.s3service.dto.PresignedUploadResponse;
import org.hexagon.core.vo.ServiceName;
import org.hexagon.s3service.entity.S3Resource;
import org.hexagon.s3service.repository.S3ResourceRepository;
import org.hexagon.s3service.vo.FileType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final S3ResourceRepository s3ResourceRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 파일을 업로드 하는 시점에(게시글 작성, 회원 정보 수정 등은 완료 x) 업로드 Presigned URL 생성(/temp에 업로드)
    public PresignedUploadResponse createUploadUrl(ServiceName serviceName, String filename, String contentType) {
        String service = serviceName.toLower();
        String key = service + "/temp/" + UUID.randomUUID().toString() + "-" + filename;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        // 전체 url = (버킷, region 정보) + key + queryString
        String url = presignedRequest.url().toString();

        // key + queryString만 추출
        String keyWithQuery = url.substring(url.indexOf(key));

        String queryString = keyWithQuery.substring(keyWithQuery.indexOf('?'));

        return new PresignedUploadResponse(
                key,
                queryString
        );
    }

    // 여러 개의 key에 대해 다운로드 Presigned URL 리스트 생성
    public PresignedDownloadListResponse createDownloadUrls(List<String> keys) {
        List<PresignedDownloadResponse> urls = keys.stream()
                .map(this::createDownloadUrl)
                .toList();

        return new PresignedDownloadListResponse(urls);
    }

    // 하나의 key에 대해 다운로드 Presigned URL 생성
    private PresignedDownloadResponse createDownloadUrl(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

        String url = presignedRequest.url().toString();

        // key + queryString만 추출
        String keyWithQuery = url.substring(url.indexOf(key));

        String queryString = keyWithQuery.substring(keyWithQuery.indexOf('?'));

        FileType fileType = FileType.fromKey(key);

        return new PresignedDownloadResponse(
                key,
                queryString,
                fileType
        );
    }

    public void deleteObject(String key) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);
    }

    public List<S3Resource> saveResources(String code, List<String> tempKeys) {

        return tempKeys.stream()
                .map(tempKey -> {
                    String destKey = moveFromTemp(tempKey); // temp → 바깥으로 이동
                    return saveResource(code, destKey);     // DB에는 destKey를 저장
                })
                .toList();
    }

    // /temp 에서 바깥으로 빼는 메소드
    private String moveFromTemp(String tempKey) {
        // 1) tempKey에서 /temp 제거 → destKey
        String destKey = tempKey.replace("/temp/", "/");

        // 2) 복사
        CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                .sourceBucket(bucket)
                .sourceKey(tempKey)
                .destinationBucket(bucket)
                .destinationKey(destKey)
                .build();

        s3Client.copyObject(copyRequest);

        // 3) 삭제
        deleteObject(tempKey);

        return destKey;
    }

    // s3_resource 테이블에 데이터 저장
    private S3Resource saveResource(String code, String key) {
        FileType fileType = FileType.fromKey(key);
        S3Resource resource = S3Resource.builder()
                .code(code)
                .key(key)
                .fileType(fileType)
                .uploadedAt(Instant.now())
                .build();

        return s3ResourceRepository.save(resource);
    }
}
