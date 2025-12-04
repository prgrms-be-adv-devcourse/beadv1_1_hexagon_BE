package org.hexagon.s3service.service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.hexagon.s3service.dto.PresignedDownloadListResponse;
import org.hexagon.s3service.dto.PresignedDownloadResponse;
import org.hexagon.s3service.dto.PresignedUploadResponse;
import org.hexagon.s3service.dto.ServiceName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
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

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

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

    public PresignedDownloadResponse createDownloadUrl(String key) {
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

        return new PresignedDownloadResponse(
                key,
                queryString
        );
    }

    public PresignedDownloadListResponse createDownloadUrls(List<String> keys) {
        List<PresignedDownloadResponse> urls = keys.stream()
                .map(this::createDownloadUrl)
                .toList();

        return new PresignedDownloadListResponse(urls);
    }

    public void deleteObject(String key) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);
    }
}
